(ns simtwop-web.routes.home
  (:require [clj-time.core :as t])
  (:require [aprint.core :refer :all])
  (:require [clojure.tools.logging :as log])
  (:require [clojure.walk :refer [keywordize-keys]])
  (:require [simtwop-web.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [simtwop-web.domain.core :as c]
            [simtwop-web.db.core :as db]
            [simtwop-web.domain.portfolio :as p]
            [simtwop-web.domain.people :as ps]
            [hiccup.core :as h]
            [hiccup.form :as f]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))
  
(defn- attributes-for [role grade]
  {:class (str "staffing_plans_role_cell staffing_plans_role_cell_count open_role_background_" role)
   :id (str grade "_" role)})

(defn- staffing-count-cell-for [role grade beach]
  (let [actual-count (or (beach (keyword (str "beach-" grade "_" role))) 0)]
    [:span
      [:td (attributes-for role grade) actual-count]
      (f/hidden-field (str "beach-" grade "_" role) actual-count)]))

(defn- staffing-count-row-for [role beach]
  [:tr {:class "staffing_plans_role"}
    [:td {:class (str "staffing_plans_role_cell open_role_background_" role)} role]
    (staffing-count-cell-for role "grad"      beach)
    (staffing-count-cell-for role "con"       beach)
    (staffing-count-cell-for role "senior"    beach)
    (staffing-count-cell-for role "lead"      beach)
    (staffing-count-cell-for role "principal" beach)])

(defn- format-beach-table [beach]
  (h/html
    [:table {:class "staffing_table"}
      [:thead
        [:tr
          [:th]
          (for [grade '["Grad" "Con" "Senior" "Lead" "Principal"]]
            [:th {:class "staff_table_grade_header"} grade])]]
      [:tbody
        (staffing-count-row-for "ba" beach)
        (staffing-count-row-for "dev" beach)
        (staffing-count-row-for "pm" beach)
        (staffing-count-row-for "qa" beach)
        (staffing-count-row-for "specialist" beach)
        (staffing-count-row-for "ux" beach) ]]))

(defn- augment-project [project date-stream]
  (-> 
    project
    (assoc :lead-time (range (project :delay-weeks)))
    (assoc :duration (range (project :duration-weeks)))
    (assoc :created-on (t/now))
    (assoc :date-stream date-stream) ))

(defn- augment-beach [beach project]
  beach)
  ; (cons beach [:created-on (t/now)]))

(def timeline (c/generate-date-stream (t/now) (t/plus (t/now) (t/weeks 36))))

(defn jigsaw 
  ([generation] (jigsaw generation (first (db/load-last-beach))))
  ([generation beach]
    (let [raw-project (p/demand-generate)
          complete-project (augment-project raw-project timeline)
          old-projects (doall (db/load-projects))
          saved-project (db/save-project complete-project)
          beach-table (format-beach-table beach)
          ]
      
    	(layout/render "jigsaw.html" {
        :old-projects old-projects
        :project saved-project
        :generation generation
        :beach-table beach-table}))))

(defn- add-assigments-to-project [project assignments]
  (aprint assignments)
  (aprint project)
  project)
  ; (assoc-in project [:spots :assigned] assignments))

(defn submit-score [project-id generation assignments]
  (let [unassigned-project (db/load-project project-id)
        assigned-project (p/update-assignments unassigned-project assignments)]
    (log/info (str "Submitting score for project " project-id ", generation " generation))

    (aprint assigned-project)
    (aprint (db/update-project assigned-project))
    (response/found (str "/" generation))))

(defn- update-beach [project-id generation beach-counts]
  (log/info (str "Updating beach from project " project-id ", generation " generation))
  (let [complete-beach (augment-beach beach-counts nil)]
    (db/save-beach complete-beach)))

(defroutes home-routes
  (POST "/:generation"  [generation]  
    (fn [req]
      (let [next-generation (+ (Integer/parseInt generation) 1)
            project-id      (get (:form-params req) "project-id")
            assignments     (keywordize-keys (into {} (filter #(re-matches #"role\-.*" (key %)) (:form-params req))))
            beach-counts   (filter #(re-matches #"beach\-.*"    (key %)) (:form-params req))]
        (update-beach project-id next-generation beach-counts)
        (submit-score project-id next-generation assignments))))
  (GET  "/:generation"  [generation]  (jigsaw generation))
  (GET  "/"             []            (jigsaw 1 (ps/initial-population))))

