(ns kaiden-player.auth
  (:require [ring.util.response :refer [response redirect]]
            [kaiden-player.layout :as layout]))

(def authdata
  "Global var that stores valid users with their
   respective passwords."
  {:admin "secret"
   :test "secret"})

(defn login
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
        found-password (get authdata (keyword email))]
    (if (and found-password (= found-password password))
      (let [updated-session (assoc session :identity (keyword email))]
        (assoc (redirect "/") :session updated-session))
      (layout/render "login.html"))))

(defn logout
  [_]
  (assoc (redirect "/login") :session {}))
