(ns kaiden-player.test.routes.home
  (:require [clojure.test :refer :all]
            [kaiden-player.routes.home :refer [song-link get-song-titles]]
            [amazonica.aws.s3 :as s3]))

(defn mock-list-objects [rv]
  (fn [cred bucket-key bucket-name]
    (is (map? cred))
    (is (= :bucket-name bucket-key))
    (is (= "kaiden-player" bucket-name))
    rv))

(deftest test
  (testing "song-link"
    (let [expected "/songs/this%20is%20a%20test.mp3"]
      (is (= expected (song-link "this is a test")))))

  (testing "get-song-titles"
    (let [expected ["song 1" "song 2"]
          input {:object-summaries [{:key "song 1"} {:key "song 2"}]}]
      (with-redefs [s3/list-objects (mock-list-objects input)]
        (is (= expected (get-song-titles nil)))))))
