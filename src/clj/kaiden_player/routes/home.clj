(ns kaiden-player.routes.home
  (:require [kaiden-player.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [response redirect content-type]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.http-response :as response]
            [kaiden-player.auth :refer [logout login]]))

(defn home-page [request]
  (if-not (authenticated? request)
    (redirect "/login")
    (layout/render "home.html")))

(defroutes home-routes
  (GET "/" [] home-page)
  (GET "/login" [] (layout/render "login.html"))
  (GET "/logout" [] logout)
  (POST "/login" [] login))

