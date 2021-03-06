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

(defn- mock-get
  [expected-endpoint]
  (fn [endpoint m]
    (is (= endpoint expected-endpoint))
    (is (map? m))))

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
          output (song-uploaded-successfully {:db db} [nil {"title" "test"}])
          db (:db output)] 
      (is (not (:loading db)))
      (is (not (:error-msg db)))
      (is (= (:success-msg db) "test.mp3 was uploaded successfully"))))

  (testing "song-uploaded-unsuccessfully"
    (let [song-uploaded-unsuccessfully #'kaiden-player.handlers/song-uploaded-unsuccessfully
          cofx {:db {:loading true}}
          cofx (song-uploaded-unsuccessfully cofx)
          db (:db cofx)]
      (is (not (:loading db)))
      (is (= (:error-msg db) "Error uploading song")))) 

  (testing "load-songs"
    (let [load-songs #'kaiden-player.handlers/load-songs]
      (with-redefs [ajax.core/GET (mock-get "/songs")]
        (is (map? (load-songs nil nil))))))

  (testing "update-songs"
    (let [update-songs #'kaiden-player.handlers/update-songs
          songs ["Song1" "Song2"]
          output (update-songs {} [nil songs])]
      (is (= (:songs output) songs))))

  (testing "set-current-song"
    (let [set-current-song #'kaiden-player.handlers/set-current-song
          song "song.mp3"
          output (set-current-song {:db {}} [nil song])]
      (is (= song (get-in output [:db :current-song])))
      (is (= :play-song (get-in output [:dispatch 0])))))

  (testing "update-dancing-gif"
    (let [update-dancing-gif #'kaiden-player.handlers/update-dancing-gif
          src "giffy.gif"
          output (update-dancing-gif {:db {}} [nil src])]
      (is (= src (get-in output [:db :dancing-gif])))))

  (testing "get-dancing-gif"
    (with-redefs [ajax.core/GET #(is (and (string? %1) (map? %2)))]
      (let [get-dancing-gif #'kaiden-player.handlers/get-dancing-gif]
        (get-dancing-gif nil nil))))

  (testing "parse-giphy-response"
    (with-redefs [rand-int (fn [_] 0)]
      (let [parse-giphy-response #'kaiden-player.handlers/parse-giphy-response
            url "my-giphy.gif"
            response {"data" [{"images" {"fixed_height" {"url" url}}}]}
            output (parse-giphy-response response)]
        (is (= url output)))))

  (testing "set-music-playing"
    (let [set-music-playing #'kaiden-player.handlers/set-music-playing
          cofx {:db {:music-playing false}}
          output (set-music-playing cofx [nil true])]
      (is (get-in output [:db :music-playing])))))
