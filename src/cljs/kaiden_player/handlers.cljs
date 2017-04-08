(ns kaiden-player.handlers
  (:require [kaiden-player.db :as db]
            [re-frame.core :refer [dispatch reg-event-db]]))

(reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))
