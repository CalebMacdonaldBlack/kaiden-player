(ns kaiden-player.test-subscribers
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [kaiden-player.core :as rc]))

(deftest subscribers
  (testing "page"
    (let [db {:page        :home
              :error-msg   "test err"
              :success-msg "test success"
              :loading     true
              :current-song "song.mp3"
              :dancing-gif "dancing-ninja.gif"
              :music-playing true}
          page #'kaiden-player.subscriptions/page
          error-msg #'kaiden-player.subscriptions/error-msg
          success-msg #'kaiden-player.subscriptions/success-msg
          loading #'kaiden-player.subscriptions/loading
          current-song #'kaiden-player.subscriptions/current-song
          dancing-gif #'kaiden-player.subscriptions/dancing-gif
          music-playing #'kaiden-player.subscriptions/music-playing]
      (is (= (page db) :home))
      (is (= (error-msg db) "test err"))
      (is (= (success-msg db) "test success"))
      (is (loading db))
      (is (= (current-song db) "song.mp3"))
      (is (= (dancing-gif db) "dancing-ninja.gif"))
      (is (music-playing db)))))
