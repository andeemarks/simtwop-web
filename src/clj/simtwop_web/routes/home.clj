(ns simtwop-web.routes.home
  (:require [simtwop-web.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defn jigsaw []
	(layout/render "jigsaw.html"
		))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/jigsaw" [] (jigsaw))
  (GET "/about" [] (about-page)))

