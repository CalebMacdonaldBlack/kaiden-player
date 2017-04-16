(ns kaiden-player.subscriptions
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :page
  (fn [db _]
    (:page db)))

(reg-sub
  :error-msg
  (fn [db _]
    (:error-msg db)))
