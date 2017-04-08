(ns kaiden-player.routes.home
  (:require [kaiden-player.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.response :refer [response redirect content-type]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]))

(defn home-page [request]
  (if-not (authenticated? request)
    (redirect "/login")
    (layout/render "home.html")))

(defroutes home-routes
  (GET "/" [] home-page))

