(ns simtwop-web.db.core
  (:require [monger.core :as mg]
  					[simtwop-web.config :refer [env]])
  (:require monger.joda-time)
  (:require [monger.collection :as mc]))

(defn load-projects []
	(let [uri "mongodb://127.0.0.1/simtwop"
    		{:keys [conn db]} (mg/connect-via-uri uri)]
    (mc/find-maps db "projects")))

(defn save-project [project]
	(let [uri "mongodb://127.0.0.1/simtwop"
    		{:keys [conn db]} (mg/connect-via-uri uri)]
    (mc/insert-and-return db "projects" project)))
