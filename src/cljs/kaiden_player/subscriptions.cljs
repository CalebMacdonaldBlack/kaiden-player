(ns kaiden-player.subscriptions
  (:require [re-frame.core :refer [reg-sub]]))

(defn- page [db _] (:page db))
(defn- error-msg [db _] (:error-msg db))
(defn- success-msg [db _] (:success-msg db))
(defn- loading [db _] (:loading db))

(reg-sub :page page)
(reg-sub :error-msg error-msg)
(reg-sub :success-msg success-msg)
(reg-sub :loading loading)
