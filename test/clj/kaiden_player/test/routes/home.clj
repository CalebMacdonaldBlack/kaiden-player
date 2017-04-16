(ns kaiden-player.test.routes.home
  (:require [clojure.test :refer :all]
            [kaiden-player.routes.home :refer [song-link]]))

(deftest test
  (testing "song-link"
    (let [expected "/songs/this%20is%20a%20test.mp3"]
      (is (= expected (song-link "this is a test"))))))
