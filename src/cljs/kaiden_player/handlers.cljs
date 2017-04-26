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
                  :handler #(rf/dispatch [:song-uploaded-successfully song-data])})
  (-> db
      (assoc :loading true)
      (dissoc :error-msg)
      (dissoc :success-msg)))

(defn- song-not-found [db _]
  (-> db
      (dissoc :success-msg)
      (assoc :error-msg "Song was not found at that link!")))

(defn- song-uploaded-successfully [db [_ response]]
  (-> db
      (dissoc :loading)
      (dissoc :error-msg)
      (dissoc :songs)
      (assoc :success-msg (str (get response "title") ".mp3 was uploaded successfully"))))

(defn- update-songs
  [db [_ songs]]
  (assoc db :songs (vec songs)))

(defn- load-songs [_ _]
  (GET "/songs" {:handler #(rf/dispatch [:update-songs %])})
  {})

(defn- next-song [cofx [_ current-song]]
  (let [db (:db cofx)
        songs (:songs db)
        index (inc (.indexOf songs current-song))]
    {:db (if (= index (count songs))
           (assoc db :current-song (first songs))
           (assoc db :current-song (nth songs index)))
     :dispatch [:play-song]}))

(defn- play-song [_ _]
  (do
    (rf/dispatch [:get-dancing-gif])
    (.load (.getElementById js/document "player")))
  {})


(defn- set-current-song
  [cofx [_ song]]
  {:db (assoc (:db cofx) :current-song song)
   :dispatch [:play-song]})

(defn- get-dancing-gif [_ _]
  (GET "http://api.giphy.com/v1/gifs/search?q=dancing+cartoon&limit=100&api_key=dc6zaTOxFJmzC"
       {:handler (fn [response]
                   (let [data (get response "data")
                         index (rand-int (count data))
                         img-src (get-in data [index "images" "fixed_height" "url"])]
                     (rf/dispatch [:update-dancing-gif img-src])))})
  {})

(defn- update-dancing-gif
  [cofx [_ src]]
  {:db (assoc (:db cofx) :dancing-gif src)})

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

(reg-event-db
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
