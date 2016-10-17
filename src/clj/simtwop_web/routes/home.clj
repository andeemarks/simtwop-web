(ns simtwop-web.routes.home
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

(defn about-page []
  (layout/render "about.html"))

(defn jigsaw []
  (let [project (p/demand-generate)
        date-stream (c/generate-date-stream (project :start-date) (project :end-date))]
    ; (spit "target/jigsaw.html" (format-upcoming-project project))]

  	(layout/render "jigsaw.html" {:date-stream date-stream})))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/jigsaw" [] (jigsaw))
  (GET "/about" [] (about-page)))

