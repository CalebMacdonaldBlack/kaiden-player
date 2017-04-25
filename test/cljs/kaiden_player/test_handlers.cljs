(ns kaiden-player.test-handlers
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [kaiden-player.core :as rc]))

(defn- mock-post
  [expected-song-endpoint expected-request]
  (fn [song-endpoint request]
    (is (= expected-song-endpoint song-endpoint))
    (is (= expected-request (:params request)))
    nil))

(deftest handlers
  (testing "test-set-active-page"
    (let [set-active-page #'kaiden-player.handlers/set-active-page
          db (set-active-page {} [nil :home])]
      (is (= db {:page :home}))))

  (testing "initialize-db"
    (let [init-db #'kaiden-player.handlers/init-db
          db {:page :home}]
      (is (= (init-db) db))))

  (testing "add-song"
    (let [expected-song-data {:test "data"}]
      (with-redefs [ajax.core/POST (mock-post "/songs" expected-song-data)]
                   (let [add-song #'kaiden-player.handlers/add-song
                         db {:error-msg "test" :success-msg "test"}
                         db (add-song db [nil expected-song-data])]
                     (is (:loading db))
                     (is (not (:error-msg db)))
                     (is (not (:success-msg db)))))))

  (testing "song-not-found"
    (let [song-not-found #'kaiden-player.handlers/song-not-found
          db {:success-msg "test"}
          db (song-not-found db)]
      (is (not (:success-msg db)))
      (is (= (:error-msg db) "Song was not found at that link!"))))

  (testing "song-uploaded-successfully"
    (let [song-uploaded-successfully #'kaiden-player.handlers/song-uploaded-successfully
          db {:loading true :error-msg "test"}
          db (song-uploaded-successfully db [nil {"title" "test"}])]
      (is (not (:loading db)))
      (is (not (:error-msg db)))
      (is (= (:success-msg db) "test.mp3 was uploaded successfully")))))
