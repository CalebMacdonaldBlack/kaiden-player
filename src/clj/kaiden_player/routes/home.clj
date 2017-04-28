(ns kaiden-player.routes.home
  (:require [kaiden-player.layout :as layout]
            [amazonica.aws.s3 :as s3]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [response redirect content-type]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.http-response :as response]
            [kaiden-player.auth :refer [logout login]]
            [clojure.java.io :as io]
            [ring.util.codec :refer [url-encode]]
            [clojure.tools.logging :as log])
  (:import (com.amazonaws SdkClientException)))

(def cred {:endpoint "ap-southeast-2"})

(defn home-page [request]
  (if-not (authenticated? request)
    (redirect "/login")
    (layout/render "home.html")))

(defn song-link [title]
  (str "/songs/" (url-encode title) ".mp3"))

(defn upload-song
  ([request]
   (upload-song request 1))
  ([request repeat-count]
   (log/debug (str "Attempt: " repeat-count))
   (try
     (let [link (get (:params request) "link")
           title (get (:params request) "title")
           file-size (get (:params request) "filesize")]
       (log/debug (str "Uploading \nURL: " link
                       "\nTitle: " title
                       "\nSize: " file-size))
       (let [song-stream (clojure.java.io/input-stream link)]
         (response/ok (s3/put-object cred :bucket-name "kaiden-player"
                                     :key (str title ".mp3")
                                     :input-stream song-stream
                                     :metadata {:content-length (read-string file-size)}
                                     :return-values "ALL_OLD")))
       (song-link title))
     (catch SdkClientException e (if (> repeat-count 5)
                                     (throw e)
                                     (do (Thread/sleep 3000)
                                         (upload-song request (inc repeat-count))))))))

(defn get-song-titles [_]
  (let [list (:object-summaries (s3/list-objects cred :bucket-name "kaiden-player"))]
    (map :key list)))

(defn get-song [request]
  (let [title (get-in request [:params :title])]
    (:input-stream (s3/get-object "kaiden-player" title))))


(defroutes home-routes
           (GET "/" [] home-page)
           (GET "/login" [] (layout/render "login.html"))
           (GET "/logout" [] logout)
           (POST "/login" [] login)
           (POST "/songs" [] #(response/created (upload-song %)))
           (GET "/songs" [] #(response/ok (get-song-titles %)))
           (GET "/songs/:title" [] #(response/ok (get-song %)))
           (GET "/test" [] (io/input-stream "test.mp3")))
