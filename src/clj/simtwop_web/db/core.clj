(ns simtwop-web.db.core
  (:require [monger.core :as mg]
  					[simtwop-web.config :refer [env]])
  (:require monger.joda-time)
  (:require [monger.collection :as mc]))

(def db-url (env :database-url))

(defn load-projects []
	(let [{:keys [conn db]} (mg/connect-via-uri db-url)]
    (mc/find-maps db "projects")))

(defn save-project [project]
	(let [{:keys [conn db]} (mg/connect-via-uri db-url)]
    (mc/insert-and-return db "projects" project)))
