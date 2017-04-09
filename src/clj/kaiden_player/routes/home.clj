(ns kaiden-player.routes.home
  (:require [kaiden-player.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [response redirect content-type]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.http-response :as response]))

(def authdata
  "Global var that stores valid users with their
   respective passwords."
  {:admin "secret"
   :test "secret"})

(defn post-login
  "Check request username and password against authdata
  username and passwords.
  On successful authentication, set appropriate user
  into the session and redirect to the value of
  (:next (:query-params request)). On failed
  authentication, renders the login page."
  [request]
  (let [email (get-in request [:form-params "email"])
        password (get-in request [:form-params "password"])
        session (:session request)
        test (prn (str email "  " password))
        found-password (get authdata (keyword email))]
    (if (and found-password (= found-password password))
      (do (prn "GRANTED")
          (let [updated-session (assoc session :identity (keyword email))]
            (-> (redirect "/")
                (assoc :session updated-session))))
      (do (prn "DENIED")
          (layout/render "login.html")))))

(defn logout
  [_]
  (-> (redirect "/login")
      (assoc :session {})))

(defn home-page [request]
  (if-not (authenticated? request)
    (redirect "/login")
    (layout/render "home.html")))

(defroutes home-routes
  (GET "/" [] home-page)
  (GET "/login" [] (layout/render "login.html"))
  (GET "/logout" [] logout)
  (POST "/login" [] post-login))

