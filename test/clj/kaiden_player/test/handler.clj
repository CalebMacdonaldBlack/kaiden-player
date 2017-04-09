(ns kaiden-player.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [kaiden-player.handler :refer :all]))

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
      (is (= 404 (:status response))))))
