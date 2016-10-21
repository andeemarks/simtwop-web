(ns simtwop-web.routes.home
  (:require [clj-time.core :as t])
  (:require [simtwop-web.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [simtwop-web.domain.core :as c]
            [simtwop-web.domain.portfolio :as p]
            [hiccup.core :as h]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn jigsaw []
  (let [project (p/demand-generate)
        date-stream (c/generate-date-stream (t/now) (project :end-date))
        roles (project :spots)]

  	(layout/render "jigsaw.html" {:date-stream date-stream :lead-time (range 0 (project :delay-weeks)) :project project :duration (range 0 (project :duration-weeks))})))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/jigsaw" [] (jigsaw)))

