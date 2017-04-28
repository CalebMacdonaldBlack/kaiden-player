(ns kaiden-player.subscriptions
  (:require [re-frame.core :refer [reg-sub] :as rf]))

(defn- page [db _] (:page db))
(defn- error-msg [db _] (:error-msg db))
(defn- success-msg [db _] (:success-msg db))
(defn- loading [db _] (:loading db))
(defn- current-song [db _] (:current-song db))
(defn- songs [db _]
  (let [songs (:songs db)]
    (if (nil? songs)
        (do (rf/dispatch [:load-songs])
            ["loading songs"])
        songs)))

(defn- dancing-gif [db _]
  (:dancing-gif db))

(defn- music-playing [db _]
  (:music-playing db))

(defn- rotate [v n]
  (let [cv (count v), n (mod n cv)]
    (concat (subvec v n cv) (subvec v 0 n))))

(defn- rotated-songs [db _]
  (let [current-song (:current-song db)
        songs (:songs db)
        index (.indexOf songs current-song)]
    (rotate songs index)))
(reg-sub :page page)
(reg-sub :error-msg error-msg)
(reg-sub :success-msg success-msg)
(reg-sub :loading loading)
(reg-sub :songs songs)
(reg-sub :current-song current-song)
(reg-sub :dancing-gif dancing-gif)
(reg-sub :music-playing music-playing)
(reg-sub :rotated-songs rotated-songs)
