(ns kaiden-player.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [ring.util.codec :refer [url-encode]]
            [kaiden-player.handler :refer :all]
            [kaiden-player.routes.home :as home-routes]))

(defn mock-upload-song [request]
  (prn request)
  (prn (:params request))
  (str "/songs/" (url-encode (get-in request [:params :title] ".mp3"))))

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

  (testing "songs"
    (with-redefs [home-routes/upload-song mock-upload-song]
      (let [response ((app) (request :post "/songs" {"filesize" "4712000"
                                                     "length" "195"
                                                     "link" "http://www.youtubeinmp3.com/download/get/?i=u7WIXh3ZF6YZPEvYZCqCuRcnvVuYSX96&e=92"
                                                     "title" "Rednex - Cotton Eye Joe"}))]
        (is (= 201 (:status response)))))))
