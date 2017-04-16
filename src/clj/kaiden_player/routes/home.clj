(ns kaiden-player.routes.home
  (:require [kaiden-player.layout :as layout]
            [amazonica.aws.s3 :as s3]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [response redirect content-type]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.http-response :as response]
            [kaiden-player.auth :refer [logout login]]
            [clojure.java.io :as io]
            [ring.util.codec :refer [url-encode]]))

(def cred {:endpoint "ap-southeast-2"})

(defn home-page [request]
  (if-not (authenticated? request)
    (redirect "/login")
    (layout/render "home.html")))

(defn song-link [title]
  (str "/songs/" (url-encode title) ".mp3"))

(defn upload-song [request]
  (let [link (get (:params request) "link")
        title (get (:params request) "title")
        file-size (get (:params request) "filesize")]
    (let [song-stream (clojure.java.io/input-stream link)]
      (response/ok (s3/put-object cred :bucket-name "kaiden-player"
                                  :key (str title ".mp3")
                                  :input-stream song-stream
                                  :metadata {:content-length (read-string file-size)}
                                  :return-values "ALL_OLD")))
    (song-link title)))

(defroutes home-routes
           (GET "/" [] home-page)
           (GET "/login" [] (layout/render "login.html"))
           (GET "/logout" [] logout)
           (POST "/login" [] login)
           (POST "/songs" [] #(response/created (upload-song %)))
           (GET "/test" [] (io/input-stream "test.mp3")))
