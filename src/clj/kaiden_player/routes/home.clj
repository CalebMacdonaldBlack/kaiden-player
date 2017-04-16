(ns kaiden-player.routes.home
  (:require [kaiden-player.layout :as layout]
            [amazonica.aws.s3 :as s3]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [response redirect content-type]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.http-response :as response]
            [kaiden-player.auth :refer [logout login]]))

(def cred {:endpoint "ap-southeast-2"})

(defn home-page [request]
  (if-not (authenticated? request)
    (redirect "/login")
    (layout/render "home.html")))

(defn songs [request]
  (prn request)
  (let [link (get (:params request) "link")
        title (get (:params request) "title")]
    (prn link)
    (let [song-stream (clojure.java.io/input-stream link)]
      (prn song-stream)
      (s3/put-object cred :bucket-name "kaiden-player"
                          :key (str title ".mp3")
                          :input-stream song-stream
                         ;:metadata {:content-length 3290000}
                          :return-values "ALL_OLD"))
    (response/ok)))

(defroutes home-routes
           (GET "/" [] home-page)
           (GET "/login" [] (layout/render "login.html"))
           (GET "/logout" [] logout)
           (POST "/login" [] login)
           (POST "/songs" [] songs))

;; put object from stream
(def some-bytes (.getBytes "Amazonica" "UTF-8"))
(def input-stream (java.io.ByteArrayInputStream. some-bytes))
