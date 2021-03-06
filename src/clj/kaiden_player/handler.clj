(ns kaiden-player.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [kaiden-player.layout :refer [error-page]]
            [kaiden-player.routes.home :refer [home-routes]]
            [compojure.route :as route]
            [kaiden-player.env :refer [defaults]]
            [mount.core :as mount]
            [kaiden-player.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (wrap-routes #'home-routes middleware/wrap-formats)
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
