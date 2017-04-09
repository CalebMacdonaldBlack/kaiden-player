(ns kaiden-player.middleware
  (:require [kaiden-player.env :refer [defaults]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.backends :as backends]
            [buddy.auth :refer [authenticated?]]
            [clojure.tools.logging :as log]
            [kaiden-player.layout :refer [*app-context* error-page]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [ring.util.response :refer [response redirect content-type]]
            [muuntaja.middleware :refer [wrap-format wrap-params]]
            [kaiden-player.config :refer [env]]
            [ring.middleware.flash :refer [wrap-flash]]
            [kaiden-player.layout :as layout]
            [immutant.web.middleware :refer [wrap-session]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [clojure.java.io :as io])
  (:import [javax.servlet ServletContext]))

(defn wrap-context [handler]
  (fn [request]
    (binding [*app-context*
              (if-let [context (:servlet-context request)]
                ;; If we're not inside a servlet environment
                ;; (for example when using mock requests), then
                ;; .getContextPath might not exist
                (try (.getContextPath ^ServletContext context)
                     (catch IllegalArgumentException _ context))
                ;; if the context is not specified in the request
                ;; we check if one has been specified in the environment
                ;; instead
                (:app-context env))]
      (handler request))))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t)
        (error-page {:status 500
                     :title "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained gnomes to take care of the problem."})))))

(defn wrap-formats [handler]
  (let [wrapped (-> handler wrap-params wrap-format)]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(def auth-backend (session-backend))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      (wrap-authentication auth-backend)
      wrap-webjars
      wrap-flash
      (wrap-session {:cookie-attrs {:http-only true}})
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (dissoc :session)))
      wrap-context
      wrap-internal-error))
