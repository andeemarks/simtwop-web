(ns simtwop-web.routes.home
  (:require [clj-time.core :as t])
  (:require [aprint.core :refer :all])
  (:require [simtwop-web.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [simtwop-web.domain.core :as c]
            [simtwop-web.domain.portfolio :as p]
            [simtwop-web.domain.people :as ps]
            [hiccup.core :as h]
            [hiccup.form :as f]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))
  
(defn- styles-for [role]
  {:class (str "staffing_plans_role_cell staffing_plans_role_cell_count open_role_background_" role)})

(defn- staffing-count-row-for [role count]
  [:td (styles-for role) (or count 0)
    (f/hidden-field "count" count)])

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
          (staffing-count-row-for "ba" (people '(:ba :grad)))
          (staffing-count-row-for "ba" (people '(:ba :con)))
          (staffing-count-row-for "ba" (people '(:ba :senior)))
          (staffing-count-row-for "ba" (people '(:ba :lead)))
          (staffing-count-row-for "ba" (people '(:ba :principal)))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_dev"} "dev"]
          (staffing-count-row-for "dev" (people '(:dev :grad)))
          (staffing-count-row-for "dev" (people '(:dev :con)))
          (staffing-count-row-for "dev" (people '(:dev :senior)))
          (staffing-count-row-for "dev" (people '(:dev :lead)))
          (staffing-count-row-for "dev" (people '(:dev :principal)))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_pm"} "pm"]
          (staffing-count-row-for "pm" (people '(:pm :grad)))
          (staffing-count-row-for "pm" (people '(:pm :con)))
          (staffing-count-row-for "pm" (people '(:pm :senior)))
          (staffing-count-row-for "pm" (people '(:pm :lead)))
          (staffing-count-row-for "pm" (people '(:pm :principal)))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_qa"} "qa"]
          (staffing-count-row-for "qa" (people '(:qa :grad)))
          (staffing-count-row-for "qa" (people '(:qa :con)))
          (staffing-count-row-for "qa" (people '(:qa :senior)))
          (staffing-count-row-for "qa" (people '(:qa :lead)))
          (staffing-count-row-for "qa" (people '(:qa :principal)))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_specialist"} "specialist"]
          (staffing-count-row-for "specialist" (people '(:specialist :grad)))
          (staffing-count-row-for "specialist" (people '(:specialist :con)))
          (staffing-count-row-for "specialist" (people '(:specialist :senior)))
          (staffing-count-row-for "specialist" (people '(:specialist :lead)))
          (staffing-count-row-for "specialist" (people '(:specialist :principal)))]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_ux"} "ux"]
          (staffing-count-row-for "ux" (people '(:ux :grad)))
          (staffing-count-row-for "ux" (people '(:ux :con)))
          (staffing-count-row-for "ux" (people '(:ux :senior)))
          (staffing-count-row-for "ux" (people '(:ux :lead)))
          (staffing-count-row-for "ux" (people '(:ux :principal)))]
          ]]))

(defn jigsaw [generation]
  (let [project (p/demand-generate)
        date-stream (c/generate-date-stream (t/now) (project :end-date))
        people (ps/ps-frequencies (ps/ps-populate 100))
        people-table (format-people-table people)
        _ (aprint people)
        _ (aprint people-table)
        roles (project :spots)]

  	(layout/render "jigsaw.html" {
      :date-stream date-stream 
      :lead-time (range (project :delay-weeks)) 
      :project project 
      :people people
      :generation generation
      :people-table people-table
      :roles ["BA" "Dev" "PM" "QA" "Specialist" "UX"]
      :grades ["Grad" "Con" "Senior" "Lead" "Principal"]
      :duration (range (project :duration-weeks))})))

(defroutes home-routes
  (POST "/:generation" [generation] (jigsaw (+ (Integer/parseInt generation) 1)))
  (GET "/" [] (jigsaw 1)))

