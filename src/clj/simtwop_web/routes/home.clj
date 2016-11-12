(ns simtwop-web.routes.home
  (:require [clj-time.core :as t])
  (:require [aprint.core :refer :all])
  (:require [clojure.tools.logging :as log])
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

(defn- staffing-count-row-for [role grade beach]
  (let [actual-count (or (beach (keyword (str "beach-" grade "_" role))) 0)]
    [:span
      [:td (attributes-for role grade) actual-count]
      (f/hidden-field (str "beach-" grade "_" role) actual-count)]))

(defn- format-beach-table [beach]
  (h/html
    [:table {:class "staffing_table"}
      [:thead
        [:tr
          [:th]
          (for [grade '["Grad" "Con" "Senior" "Lead" "Principal"]]
            [:th {:class "staff_table_grade_header"} grade])]]
      [:tbody
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_ba"} "ba"]
          (staffing-count-row-for "ba" "grad"      beach)
          (staffing-count-row-for "ba" "con"       beach)
          (staffing-count-row-for "ba" "senior"    beach)
          (staffing-count-row-for "ba" "lead"      beach)
          (staffing-count-row-for "ba" "principal" beach)]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_dev"} "dev"]
          (staffing-count-row-for "dev" "grad"      beach)
          (staffing-count-row-for "dev" "con"       beach)
          (staffing-count-row-for "dev" "senior"    beach)
          (staffing-count-row-for "dev" "lead"      beach)
          (staffing-count-row-for "dev" "principal" beach)]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_pm"} "pm"]
          (staffing-count-row-for "pm" "grad"      beach)
          (staffing-count-row-for "pm" "con"       beach)
          (staffing-count-row-for "pm" "senior"    beach)
          (staffing-count-row-for "pm" "lead"      beach)
          (staffing-count-row-for "pm" "principal" beach)]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_qa"} "qa"]
          (staffing-count-row-for "qa" "grad"      beach)
          (staffing-count-row-for "qa" "con"       beach)
          (staffing-count-row-for "qa" "senior"    beach)
          (staffing-count-row-for "qa" "lead"      beach)
          (staffing-count-row-for "qa" "principal" beach)]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_specialist"} "specialist"]
          (staffing-count-row-for "specialist" "grad"      beach)
          (staffing-count-row-for "specialist" "con"       beach)
          (staffing-count-row-for "specialist" "senior"    beach)
          (staffing-count-row-for "specialist" "lead"      beach)
          (staffing-count-row-for "specialist" "principal" beach)]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_ux"} "ux"]
          (staffing-count-row-for "ux" "grad"      beach)
          (staffing-count-row-for "ux" "con"       beach)
          (staffing-count-row-for "ux" "senior"    beach)
          (staffing-count-row-for "ux" "lead"      beach)
          (staffing-count-row-for "ux" "principal" beach)]
          ]]))

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
  project)
  ; (assoc-in project [:spots :assigned] assignments))

(defn submit-score [project-id generation assignments]
  (let [unassigned-project (db/load-project project-id)
        assigned-project (add-assigments-to-project unassigned-project assignments)]
    (log/info (str "Submitting score for project " project-id ", generation " generation))

    ; (aprint assigned-project)
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
            assignments     (filter #(re-matches #"role\-.*" (key %)) (:form-params req))
            beach-counts   (filter #(re-matches #"beach\-.*"    (key %)) (:form-params req))]
        (update-beach project-id next-generation beach-counts)
        (submit-score project-id next-generation assignments))))
  (GET  "/:generation"  [generation]  (jigsaw generation))
  (GET  "/"             []            (jigsaw 1 (ps/initial-population))))

