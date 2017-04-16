(ns kaiden-player.handlers
  (:require [kaiden-player.db :as db]
            [ajax.core :refer [POST]]
            [re-frame.core :refer [dispatch reg-event-db reg-event-fx] :as rf]))

(reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))

(reg-event-fx
  :add-song
  (fn [_ [_ song-data]]
    (prn song-data)
    (POST "/songs" {:params song-data
                    :handler #(rf/dispatch [:song-uploaded-successfully song-data])})))

(reg-event-db
  :song-not-found
  (fn [db _]
    (-> db
      (dissoc :success-msg)
      (assoc :error-msg "Song was not found at that link!"))))

(reg-event-db
  :song-uploaded-successfully
  (fn [db [_ response]]
    (-> db
      (dissoc :error-msg)
      (assoc :success-msg (str (get response "title") ".mp3 was uploaded successfully")))))
