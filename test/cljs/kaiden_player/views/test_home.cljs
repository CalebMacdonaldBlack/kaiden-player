(ns kaiden-player.views.test-home
  (:require [com.rpl.specter :refer [select keypath walker]]
            [ajax.core :refer [GET]]
            [pjstadig.humane-test-output]
            [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [re-frame.core :as rf]))

(defn- mock-subscribe
  [coll error-msg success-msg loading]
  (case (first coll)
    :error-msg (atom error-msg)
    :loading (atom loading)
    :success-msg (atom success-msg))) 

(deftest test-home-view
  (let [url-input #'kaiden-player.views.home/url-input]
    (testing "url-input value"
      (let [expected "url"
            actual (first (select (keypath 1 :value) (url-input (atom true) (atom expected))))]
        (is (= expected actual))))

    (testing "url-input loading"
      (let [expected false
            actual (first (select (keypath 1 :disabled) (url-input (atom expected) (atom ""))))]
        (is (= expected actual))))))

(deftest test-message
  (let [message #'kaiden-player.views.home/message]
    (testing "error message"
      (let [expected "test error"
            hiccup-output (message (atom false) (atom expected) (atom "test-message"))
            actual (first (select (walker #(= % expected)) hiccup-output))]
        (is (= expected actual))))

    (testing "success message"
      (let [expected "test success message"
            hiccup-output (message (atom false) (atom "test error") (atom expected))
            actual (first (select (walker #(= % expected)) hiccup-output))]
        (is (= expected actual))))

    (testing "loading indicator"
      (let [expected "/img/loading.gif"
            hiccup-output (message (atom true) (atom "") (atom ""))
            actual (first (select (walker #(= % expected)) hiccup-output))]
        (is (= expected actual))))

    (testing "no message"
      (let [hiccup-output (message (atom false) (atom nil) (atom nil))
            matches (select (walker #(or (= :img %)
                                         (= :small %)))
                            hiccup-output)]
        (is (empty? matches))))))

(deftest test-submit-button
  (let [url (atom "mysong/test.com")
        endpoint "mybackend.com/addsong?songurl="
        submit-button #'kaiden-player.views.home/submit-button
        hiccup-output (submit-button (atom true) url endpoint)]
    (testing "submit-button disabled"
      (let [loading? (first (select (keypath 1 :disabled) hiccup-output))]
        (is loading?)))

    (testing "submit button callback"
      (let [expected "mybackend.com/addsong?songurl=mysong%2Ftest.com"]
        (with-redefs [GET (fn [full-url _] (is (= full-url expected)))]
          ((first (select (keypath 1 :on-click) hiccup-output))))))))

(deftest test-handlers
  (let [youtubeinmp3-handler #'kaiden-player.views.home/youtubeinmp3-handler
        youtubeinmp3-error-handler #'kaiden-player.views.home/youtubeinmp3-error-handler
        url (atom "testurl.com")
        response {:test "response"}]
    (testing "success handler"
      (with-redefs [rf/dispatch (fn [param] (is (= (second param) response)))]
        ((youtubeinmp3-handler url) response)
        (is (= "" @url))
        (youtubeinmp3-error-handler response)))))

(deftest test-view
  (testing "remove-mp3-suffix"
    (let [remove-mp3-suffix #'kaiden-player.views.home/remove-mp3-suffix
          output (remove-mp3-suffix "songname.mp3")]
         (is (= output "songname")))))