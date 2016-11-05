(ns simtwop-web.db.core
  (:require [monger.core :as mg]
  					[monger.query :as q]
  					[simtwop-web.config :refer [env]])
  (:require [clojure.tools.logging :as log])
  (:require monger.joda-time)
  (:require [monger.collection :as mc]))

; (def db-url (env :database-url))
(def db-url "mongodb://127.0.0.1/simtwop")

(defn load-projects []
	(log/info "Loading all projects...")
	(let [{:keys [conn db]} (mg/connect-via-uri db-url)]
		(q/with-collection db "projects"
			(q/find {})
			(q/limit 10)
			(q/sort (array-map :created-on -1)))))

(defn load-last-beach []
	(log/info "Loading last beach structure...")
	(let [{:keys [conn db]} (mg/connect-via-uri db-url)]
		(q/with-collection db "people"
			(q/find {})
			(q/limit 1)
			(q/sort (array-map :_id -1)))))

(defn save-project [project]
	(log/info "Saving project...")
	(let [{:keys [conn db]} (mg/connect-via-uri db-url)]
  	(mc/insert-and-return db "projects" project)))

(defn save-beach [beach]
	(log/info "Saving beach...")
	(let [{:keys [conn db]} (mg/connect-via-uri db-url)]
  	(mc/insert-and-return db "people" beach)))
