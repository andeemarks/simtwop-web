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

(defn jigsaw []
  (let [project (p/demand-generate)
        date-stream (c/generate-date-stream (t/now) (project :end-date))
        people (ps/ps-populate 10)
        _ (aprint people)
        roles (project :spots)]

  	(layout/render "jigsaw.html" {
      :date-stream date-stream 
      :lead-time (range (project :delay-weeks)) 
      :project project 
      :people people
      :roles ["BA" "Dev" "PM" "QA" "Specialist" "UX"]
      :grades ["Grad" "Con" "Senior" "Lead" "Principal"]
      :duration (range (project :duration-weeks))})))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/jigsaw" [] (jigsaw)))

