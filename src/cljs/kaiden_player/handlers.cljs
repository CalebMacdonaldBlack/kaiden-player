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
      (assoc :success-msg (str (get response "title") ".mp3 was uploaded successfully"))))

(defn- update-songs
  [db [_ songs]]
  (assoc db :songs (vec songs)))

(defn- load-songs [db _]
  (GET "/songs" {:handler #(rf/dispatch [:update-songs %])})
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

(reg-event-db
  :song-uploaded-successfully
  song-uploaded-successfully)

(reg-event-fx
  :load-songs
  load-songs)

(reg-event-db
  :update-songs
  update-songs)
