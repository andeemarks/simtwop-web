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

(defn- staffing-count-row-for [role grade count]
  (let [actual-count (or count 0)]
    [:td (attributes-for role grade) actual-count
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
          (staffing-count-row-for "ba" "grad"      (beach :beach-grad_ba))
          (staffing-count-row-for "ba" "con"       (beach :beach-con_ba))
          (staffing-count-row-for "ba" "senior"    (beach :beach-senior_ba))
          (staffing-count-row-for "ba" "lead"      (beach :beach-lead_ba))
          (staffing-count-row-for "ba" "principal" (beach :beach-principal_ba))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_dev"} "dev"]
          (staffing-count-row-for "dev" "grad"      (beach :beach-grad_dev))
          (staffing-count-row-for "dev" "con"       (beach :beach-con_dev))
          (staffing-count-row-for "dev" "senior"    (beach :beach-senior_dev))
          (staffing-count-row-for "dev" "lead"      (beach :beach-lead_dev))
          (staffing-count-row-for "dev" "principal" (beach :beach-principal_dev))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_pm"} "pm"]
          (staffing-count-row-for "pm" "grad"      (beach :beach-grad_pm))
          (staffing-count-row-for "pm" "con"       (beach :beach-con_pm))
          (staffing-count-row-for "pm" "senior"    (beach :beach-senior_pm))
          (staffing-count-row-for "pm" "lead"      (beach :beach-lead_pm))
          (staffing-count-row-for "pm" "principal" (beach :beach-principal_pm))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_qa"} "qa"]
          (staffing-count-row-for "qa" "grad"      (beach :beach-grad_qa))
          (staffing-count-row-for "qa" "con"       (beach :beach-con_qa))
          (staffing-count-row-for "qa" "senior"    (beach :beach-senior_qa))
          (staffing-count-row-for "qa" "lead"      (beach :beach-lead_qa))
          (staffing-count-row-for "qa" "principal" (beach :beach-principal_qa))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_specialist"} "specialist"]
          (staffing-count-row-for "specialist" "grad"      (beach :beach-grad_specialist))
          (staffing-count-row-for "specialist" "con"       (beach :beach-con_specialist))
          (staffing-count-row-for "specialist" "senior"    (beach :beach-senior_specialist))
          (staffing-count-row-for "specialist" "lead"      (beach :beach-lead_specialist))
          (staffing-count-row-for "specialist" "principal" (beach :beach-principal_specialist))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_ux"} "ux"]
          (staffing-count-row-for "ux" "grad"      (beach :beach-grad_ux))
          (staffing-count-row-for "ux" "con"       (beach :beach-con_ux))
          (staffing-count-row-for "ux" "senior"    (beach :beach-senior_ux))
          (staffing-count-row-for "ux" "lead"      (beach :beach-lead_ux))
          (staffing-count-row-for "ux" "principal" (beach :beach-principal_ux))]
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

(defn submit-score [project-id generation assignments]
  (log/info (str "Submitting score for project " project-id ", generation " generation))

  (aprint assignments)
  (response/found (str "/" generation)))

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

