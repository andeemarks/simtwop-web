(ns simtwop-web.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [simtwop-web.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[simtwop-web started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[simtwop-web has shut down successfully]=-"))
   :middleware wrap-dev})
