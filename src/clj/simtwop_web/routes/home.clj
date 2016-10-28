(ns simtwop-web.routes.home
  (:require [clj-time.core :as t])
  (:require [aprint.core :refer :all])
  (:require [simtwop-web.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [simtwop-web.domain.core :as c]
            [simtwop-web.domain.portfolio :as p]
            [simtwop-web.domain.people :as ps]
            [hiccup.core :as h]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))
  
(defn- styles-for [role]
  {:class (str "staffing_plans_role_cell staffing_plans_role_cell_count open_role_background_" role)})

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
          [:td (styles-for "ba") (or (people '(:ba :grad)) 0)]
          [:td (styles-for "ba") (or (people '(:ba :con)) 0)]
          [:td (styles-for "ba") (or (people '(:ba :senior)) 0)]
          [:td (styles-for "ba") (or (people '(:ba :lead)) 0)]
          [:td (styles-for "ba") (or (people '(:ba :principal)) 0)]]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_dev"} "dev"]
          [:td (styles-for "dev") (or (people '(:dev :grad)) 0)]
          [:td (styles-for "dev") (or (people '(:dev :con)) 0)]
          [:td (styles-for "dev") (or (people '(:dev :senior)) 0)]
          [:td (styles-for "dev") (or (people '(:dev :lead)) 0)]
          [:td (styles-for "dev") (or (people '(:dev :principal)) 0)]]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_pm"} "pm"]
          [:td (styles-for "pm") (or (people '(:pm :grad)) 0)]
          [:td (styles-for "pm") (or (people '(:pm :con)) 0)]
          [:td (styles-for "pm") (or (people '(:pm :senior)) 0)]
          [:td (styles-for "pm") (or (people '(:pm :lead)) 0)]
          [:td (styles-for "pm") (or (people '(:pm :principal)) 0)]]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_qa"} "qa"]
          [:td (styles-for "qa") (or (people '(:qa :grad)) 0)]
          [:td (styles-for "qa") (or (people '(:qa :con)) 0)]
          [:td (styles-for "qa") (or (people '(:qa :senior)) 0)]
          [:td (styles-for "qa") (or (people '(:qa :lead)) 0)]
          [:td (styles-for "qa") (or (people '(:qa :principal)) 0)]]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_specialist"} "specialist"]
          [:td (styles-for "specialist") (or (people '(:specialist :grad)) 0)]
          [:td (styles-for "specialist") (or (people '(:specialist :con)) 0)]
          [:td (styles-for "specialist") (or (people '(:specialist :senior)) 0)]
          [:td (styles-for "specialist") (or (people '(:specialist :lead)) 0)]
          [:td (styles-for "specialist") (or (people '(:specialist :principal)) 0)]]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_ux"} "ux"]
          [:td (styles-for "ux") (or (people '(:ux :grad)) 0)]
          [:td (styles-for "ux") (or (people '(:ux :con)) 0)]
          [:td (styles-for "ux") (or (people '(:ux :senior)) 0)]
          [:td (styles-for "ux") (or (people '(:ux :lead)) 0)]
          [:td (styles-for "ux") (or (people '(:ux :principal)) 0)]]
          ]]))

(defn jigsaw []
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
      :people-table people-table
      :roles ["BA" "Dev" "PM" "QA" "Specialist" "UX"]
      :grades ["Grad" "Con" "Senior" "Lead" "Principal"]
      :duration (range (project :duration-weeks))})))

(defroutes home-routes
  (GET "/" [] (jigsaw)))

