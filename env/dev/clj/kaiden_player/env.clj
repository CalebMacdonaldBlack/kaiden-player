(ns kaiden-player.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [kaiden-player.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[kaiden-player started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[kaiden-player has shut down successfully]=-"))
   :middleware wrap-dev})
