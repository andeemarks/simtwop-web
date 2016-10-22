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
        (for [role '["ba" "dev" "pm" "qa" "specialist" "ux"]]
          [:tr {:class "staffing_plans_role"}
            [:td {:class (str "staffing_plans_role_cell open_role_background_" role)} role]
            [:td {:class (str "staffing_plans_role_cell open_role_background_" role)} (people '(:dev :grad))]
            [:td {:class (str "staffing_plans_role_cell open_role_background_" role)} (people '(:dev :con))]
            [:td {:class (str "staffing_plans_role_cell open_role_background_" role)} (people '(:dev :senior))]
            [:td {:class (str "staffing_plans_role_cell open_role_background_" role)} (people '(:dev :lead))]
            [:td {:class (str "staffing_plans_role_cell open_role_background_" role)} (people '(:dev :principal))]])]]))

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

