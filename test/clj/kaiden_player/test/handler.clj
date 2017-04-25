(ns kaiden-player.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [ring.util.codec :refer [url-encode]]
            [kaiden-player.handler :refer :all]
            [kaiden-player.routes.home :as home-routes]
            [kaiden-player.routes.home :refer [get-song-titles]]
            [ring.util.http-response :as response]
            [cheshire.core :as json]))

(defn mock-upload-song [request]
  (str "/songs/" (url-encode (get-in request [:params :title] ".mp3"))))

(defn mock-get-song-titles [_]
  ["song1" "song2"])

(defn mock-get-song [_]
  {:mock-get-song "called"})


(deftest test-app
  (testing "main route"
    (let [response ((app) (request :get "/"))]
      (is (= 302 (:status response)))))

  (testing "login route"
    (let [response ((app) (request :get "/login"))]
      (is (= 200 (:status response)))))

  (testing "login route post"
    (let [response ((app) (request :post "/login" {:email "admin" :password "secret"}))]
      (is (= 302 (:status response)))))

  (testing "logout route"
    (let [response ((app) (request :get "/logout"))]
      (is (= 302 (:status response)))))

  (testing "not-found route"
    (let [response ((app) (request :get "/invalid"))]
      (is (= 404 (:status response)))))

  (testing "post songs"
    (with-redefs [home-routes/upload-song mock-upload-song]
      (let [response ((app) (request :post "/songs" {"filesize" "4712000"
                                                     "length" "195"
                                                     "link" "http://www.youtubeinmp3.com/download/get/?i=u7WIXh3ZF6YZPEvYZCqCuRcnvVuYSX96&e=92"
                                                     "title" "Rednex - Cotton Eye Joe"}))]
        (is (= 201 (:status response))))))

  (testing "get songs"
    (with-redefs [home-routes/get-song-titles mock-get-song-titles]
      (let [response ((app) (request :get "/songs"))]
        (is (= 200 (:status response)))
        (prn response)
        (is (= ["song1" "song2"] (json/parse-string (slurp (:body response)) true))))))

  (testing "get song"
    (with-redefs [home-routes/get-song mock-get-song]
      (let [response ((app) (request :get "/songs/song1.mp3"))]
        (is (= 200 (:status response)))
        (is (= {:mock-get-song "called"} (json/parse-string (slurp (:body response)) true)))))))
