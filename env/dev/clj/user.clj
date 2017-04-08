(ns user
  (:require [mount.core :as mount]
            [kaiden-player.figwheel :refer [start-fw stop-fw cljs]]
            kaiden-player.core))

(defn start []
  (mount/start-without #'kaiden-player.core/http-server
                       #'kaiden-player.core/repl-server))

(defn stop []
  (mount/stop-except #'kaiden-player.core/http-server
                     #'kaiden-player.core/repl-server))

(defn restart []
  (stop)
  (start))


