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
      (f/hidden-field (str "count-" grade "_" role) actual-count)]))

(defn- format-people-table [people]
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
          (staffing-count-row-for "ba" "grad"      (people '(:ba :grad)))
          (staffing-count-row-for "ba" "con"       (people '(:ba :con)))
          (staffing-count-row-for "ba" "senior"    (people '(:ba :senior)))
          (staffing-count-row-for "ba" "lead"      (people '(:ba :lead)))
          (staffing-count-row-for "ba" "principal" (people '(:ba :principal)))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_dev"} "dev"]
          (staffing-count-row-for "dev" "grad"      (people '(:dev :grad)))
          (staffing-count-row-for "dev" "con"       (people '(:dev :con)))
          (staffing-count-row-for "dev" "senior"    (people '(:dev :senior)))
          (staffing-count-row-for "dev" "lead"      (people '(:dev :lead)))
          (staffing-count-row-for "dev" "principal" (people '(:dev :principal)))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_pm"} "pm"]
          (staffing-count-row-for "pm" "grad"      (people '(:pm :grad)))
          (staffing-count-row-for "pm" "con"       (people '(:pm :con)))
          (staffing-count-row-for "pm" "senior"    (people '(:pm :senior)))
          (staffing-count-row-for "pm" "lead"      (people '(:pm :lead)))
          (staffing-count-row-for "pm" "principal" (people '(:pm :principal)))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_qa"} "qa"]
          (staffing-count-row-for "qa" "grad"      (people '(:qa :grad)))
          (staffing-count-row-for "qa" "con"       (people '(:qa :con)))
          (staffing-count-row-for "qa" "senior"    (people '(:qa :senior)))
          (staffing-count-row-for "qa" "lead"      (people '(:qa :lead)))
          (staffing-count-row-for "qa" "principal" (people '(:qa :principal)))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_specialist"} "specialist"]
          (staffing-count-row-for "specialist" "grad"      (people '(:specialist :grad)))
          (staffing-count-row-for "specialist" "con"       (people '(:specialist :con)))
          (staffing-count-row-for "specialist" "senior"    (people '(:specialist :senior)))
          (staffing-count-row-for "specialist" "lead"      (people '(:specialist :lead)))
          (staffing-count-row-for "specialist" "principal" (people '(:specialist :principal)))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_ux"} "ux"]
          (staffing-count-row-for "ux" "grad"      (people '(:ux :grad)))
          (staffing-count-row-for "ux" "con"       (people '(:ux :con)))
          (staffing-count-row-for "ux" "senior"    (people '(:ux :senior)))
          (staffing-count-row-for "ux" "lead"      (people '(:ux :lead)))
          (staffing-count-row-for "ux" "principal" (people '(:ux :principal)))]
          ]]))

(defn- augment-project [project date-stream]
  (-> 
    project
    (assoc :lead-time (range (project :delay-weeks)))
    (assoc :duration (range (project :duration-weeks)))
    (assoc :created-on (t/now))
    (assoc :date-stream date-stream) ))

(def timeline (c/generate-date-stream (t/now) (t/plus (t/now) (t/weeks 36))))

(defn jigsaw [generation]
  (let [project (p/demand-generate)
        old-projects (doall (db/load-projects))
        people (ps/ps-frequencies (ps/ps-populate 100))
        people-table (format-people-table people)]
    
  	(layout/render "jigsaw.html" {
      :old-projects old-projects
      :project (db/save-project (augment-project project timeline))
      :people people
      :generation generation
      :people-table people-table})))

(defn submit-score [generation assignments]
  (log/info (str "Submitting score for generation " generation))
  (log/info assignments)
  (response/found (str "/" generation)))

(defroutes home-routes
  (POST "/:generation"  [generation]  
    (fn [req]
      (let [assignments  (filter #(re-matches #"role\-.*" (key %)) (:form-params req))
            staff-counts (filter #(re-matches #"count\-.*"    (key %)) (:form-params req))]
      (submit-score (+ (Integer/parseInt generation) 1) assignments))))
  (GET  "/:generation"  [generation]  (jigsaw generation))
  (GET  "/"             []            (jigsaw 1)))

