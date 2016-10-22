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

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

; <table class="staffing_table">
;     <thead>
;       <tr>
;         <th></th>
        
;           <th class="staff_table_grade_header"> Grad </th>
        
;           <th class="staff_table_grade_header"> Con </th>
        
;           <th class="staff_table_grade_header"> Senior </th>
        
;           <th class="staff_table_grade_header"> Lead </th>
        
;           <th class="staff_table_grade_header"> Principal </th>
        
;       </tr>
;     </thead>
;     <tbody>
        
;           <tr class="staffing_plans_role">
;             <td class="staffing_plans_role_cell open_role_background_ba">BA</td>
            
;               <td class="staffing_plans_role_cell open_role_background_ba">  </td>
            
;               <td class="staffing_plans_role_cell open_role_background_ba">  </td>
            
;               <td class="staffing_plans_role_cell open_role_background_ba">  </td>
            
;               <td class="staffing_plans_role_cell open_role_background_ba">  </td>
            
;               <td class="staffing_plans_role_cell open_role_background_ba">  </td>
            
;           </tr>
  
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
          [:td {:class "staffing_plans_role_cell open_role_background_ba"} (people '(:ba :grad))]
          [:td {:class "staffing_plans_role_cell open_role_background_ba"} (people '(:ba :con))]
          [:td {:class "staffing_plans_role_cell open_role_background_ba"} (people '(:ba :senior))]
          [:td {:class "staffing_plans_role_cell open_role_background_ba"} (people '(:ba :lead))]
          [:td {:class "staffing_plans_role_cell open_role_background_ba"} (people '(:ba :principal))]]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_dev"} "dev"]
          [:td {:class "staffing_plans_role_cell open_role_background_dev"} (people '(:dev :grad))]
          [:td {:class "staffing_plans_role_cell open_role_background_dev"} (people '(:dev :con))]
          [:td {:class "staffing_plans_role_cell open_role_background_dev"} (people '(:dev :senior))]
          [:td {:class "staffing_plans_role_cell open_role_background_dev"} (people '(:dev :lead))]
          [:td {:class "staffing_plans_role_cell open_role_background_dev"} (people '(:dev :principal))]]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_pm"} "pm"]
          [:td {:class "staffing_plans_role_cell open_role_background_pm"} (people '(:pm :grad))]
          [:td {:class "staffing_plans_role_cell open_role_background_pm"} (people '(:pm :con))]
          [:td {:class "staffing_plans_role_cell open_role_background_pm"} (people '(:pm :senior))]
          [:td {:class "staffing_plans_role_cell open_role_background_pm"} (people '(:pm :lead))]
          [:td {:class "staffing_plans_role_cell open_role_background_pm"} (people '(:pm :principal))]]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_qa"} "qa"]
          [:td {:class "staffing_plans_role_cell open_role_background_qa"} (people '(:qa :grad))]
          [:td {:class "staffing_plans_role_cell open_role_background_qa"} (people '(:qa :con))]
          [:td {:class "staffing_plans_role_cell open_role_background_qa"} (people '(:qa :senior))]
          [:td {:class "staffing_plans_role_cell open_role_background_qa"} (people '(:qa :lead))]
          [:td {:class "staffing_plans_role_cell open_role_background_qa"} (people '(:qa :principal))]]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_specialist"} "specialist"]
          [:td {:class "staffing_plans_role_cell open_role_background_specialist"} (people '(:specialist :grad))]
          [:td {:class "staffing_plans_role_cell open_role_background_specialist"} (people '(:specialist :con))]
          [:td {:class "staffing_plans_role_cell open_role_background_specialist"} (people '(:specialist :senior))]
          [:td {:class "staffing_plans_role_cell open_role_background_specialist"} (people '(:specialist :lead))]
          [:td {:class "staffing_plans_role_cell open_role_background_specialist"} (people '(:specialist :principal))]]
        [:tr {:class "staffing_plans_role"}
          [:td {:class "staffing_plans_role_cell open_role_background_ux"} "ux"]
          [:td {:class "staffing_plans_role_cell open_role_background_ux"} (people '(:ux :grad))]
          [:td {:class "staffing_plans_role_cell open_role_background_ux"} (people '(:ux :con))]
          [:td {:class "staffing_plans_role_cell open_role_background_ux"} (people '(:ux :senior))]
          [:td {:class "staffing_plans_role_cell open_role_background_ux"} (people '(:ux :lead))]
          [:td {:class "staffing_plans_role_cell open_role_background_ux"} (people '(:ux :principal))]]
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
  (GET "/" [] (home-page))
  (GET "/jigsaw" [] (jigsaw)))

