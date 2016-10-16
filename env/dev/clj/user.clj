(ns user
  (:require [mount.core :as mount]
            simtwop-web.core))

(defn start []
  (mount/start-without #'simtwop-web.core/repl-server))

(defn stop []
  (mount/stop-except #'simtwop-web.core/repl-server))

(defn restart []
  (stop)
  (start))


