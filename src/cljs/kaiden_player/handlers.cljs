(ns kaiden-player.handlers
  (:require [kaiden-player.db :as db]
            [ajax.core :refer [POST GET]]
            [re-frame.core :refer [dispatch reg-event-db reg-event-fx] :as rf]))

(defn- init-db
  [_ _]
  db/default-db)

(defn- set-active-page
  [db [_ page]]
  (assoc db :page page))

(defn- add-song
  [db [_ song-data]]
  (POST "/songs" {:params  song-data
                  :handler #(rf/dispatch [:song-uploaded-successfully song-data])
                  :error-handler #(rf/dispatch [:song-uploaded-unsuccessfully])})
  (-> db
      (assoc :loading true)
      (dissoc :error-msg)
      (dissoc :success-msg)))

(defn- song-not-found [db _]
  (-> db
      (dissoc :success-msg)
      (assoc :error-msg "Song was not found at that link!")))

(defn- song-uploaded-successfully [cofx [_ response]]
  (let [db (:db cofx)
        msg (str (get response "title") ".mp3 was uploaded successfully")]
    (prn msg)
    (prn cofx)
    {:db (-> db
          (dissoc :loading)
          (dissoc :error-msg)
          (dissoc :songs)
          (assoc :success-msg msg))
     :dispatch [:notify-success msg]}))

(defn- song-uploaded-unsuccessfully [cofx _]
  (let [db (:db cofx)
        msg "Error uploading song"]
    {:db (-> db
            (dissoc :loading)
            (assoc :error-msg msg)) 
     :dispatch [:notify-error msg]})) 

(defn- update-songs
  [db [_ songs]]
  (assoc db :songs (vec songs)))

(defn- load-songs [_ _]
  (GET "/songs" {:handler #(rf/dispatch [:update-songs %])})
  {})

(defn- next-song [cofx _]
  (let [db (:db cofx)
        songs (:songs db)
        current-song (:current-song db)
        index (inc (.indexOf songs current-song))]
    (prn (str "Before Song: " current-song " After Song: " (nth songs index) " Index of current: " (.indexOf songs current-song) " Index of next: " (inc (.indexOf songs current-song))))
    {:db (if (= index (count songs))
           (assoc db :current-song (first songs))
           (do (prn (str "settings song" (nth songs index)))
               (assoc db :current-song (nth songs index))))
     :dispatch [:play-song]}))

(defn- play-song [cofx _]
  (do
    (prn (str "Playing song: " (get-in cofx [:db :current-song])))
    (rf/dispatch [:get-dancing-gif])
    (aset js/document "title" (get-in cofx [:db :current-song]))
    (set! (.-src (.getElementById js/document "player-source")) (str "/songs/"(js/encodeURIComponent (get-in cofx [:db :current-song]))))
    (.load (.getElementById js/document "player")))
  {:db (assoc (:db cofx) :music-playing true)})


(defn- set-current-song
  [cofx [_ song]]
  {:db (assoc (:db cofx) :current-song song)
   :dispatch [:play-song]})

(defn- parse-giphy-response [response]
  (let [data (get response "data")
        index (rand-int (count data))]
    (get-in data [index "images" "fixed_height" "url"])))

(defn- get-dancing-gif [_ _]
  (GET (str "http://api.giphy.com/v1/gifs/search?q=dancing+cartoon&limit=100&api_key=dc6zaTOxFJmzC&offset=" (rand-int 100))
       {:handler #(rf/dispatch [:update-dancing-gif (parse-giphy-response %)])})

  {})

(defn- update-dancing-gif
  [cofx [_ src]]
  {:db (assoc (:db cofx) :dancing-gif src)})

(defn- set-music-playing
  [cofx [_ playing?]]
  {:db (assoc (:db cofx) :music-playing playing?)})

(defn shuffle-songs
  [cofx _]
  {:db (assoc (:db cofx) :songs (shuffle (:songs (:db cofx))))})

(defn notify-error
  [cofx [_ msg]]
  (js/Notification. "Error" (cljs.core/clj->js {"body" msg "icon" "/img/error.png"}))
  {})
  
(defn notify-success
  [cofx [_ msg]]
  (js/Notification. "Success" (cljs.core/clj->js {"body" msg "icon" "/img/success.png"}))
  {})

(reg-event-db
  :initialize-db
  init-db)

(reg-event-db
  :set-active-page
  set-active-page)

(reg-event-db
  :add-song
  add-song)

(reg-event-db
  :song-not-found
  song-not-found)

(reg-event-fx
  :song-uploaded-successfully
  song-uploaded-successfully)

(reg-event-fx
  :load-songs
  load-songs)

(reg-event-db
  :update-songs
  update-songs)

(reg-event-fx
  :set-current-song
  set-current-song)

(reg-event-fx
  :next-song
  next-song)

(reg-event-fx
  :play-song
  play-song)

(reg-event-fx
  :get-dancing-gif
  get-dancing-gif)

(reg-event-fx
  :update-dancing-gif
  update-dancing-gif)

(reg-event-fx
  :set-music-playing
  set-music-playing)

(reg-event-fx
 :shuffle-songs
 shuffle-songs)

(reg-event-fx
  :song-uploaded-unsuccessfully
  song-uploaded-unsuccessfully)

(reg-event-fx
  :notify-error
  notify-error)

(reg-event-fx
  :notify-success
  notify-success)