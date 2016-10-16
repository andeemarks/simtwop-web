(ns simtwop-web.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[simtwop-web started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[simtwop-web has shut down successfully]=-"))
   :middleware identity})
