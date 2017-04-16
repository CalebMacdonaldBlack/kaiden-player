(ns kaiden-player.handlers
  (:require [kaiden-player.db :as db]
            [ajax.core :refer [POST]]
            [re-frame.core :refer [dispatch reg-event-db reg-event-fx]]))

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
                    :handler #(prn "SUCCESS")})))

(reg-event-db
  :song-not-found
  (fn [db _]
    (assoc db :error-msg "Song was not found at that link!")))
