(ns kaiden-player.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [kaiden-player.ajax :refer [load-interceptors!]]
            [kaiden-player.handlers]
            [kaiden-player.subscriptions])
  (:import goog.History))

(defn nav-link [uri title page]
  (let [selected-page (rf/subscribe [:page])]
    [:a.item
     {:class (when (= page @selected-page) "active")
      :href uri}
     title]))

(defn navbar []
  [:div.ui.menu
   [:div.header.item "Kaiden Player"]
   (nav-link "#/" "Home" :home)
   [:div.right.menu
    [:a.item {:href "/logout"} "Logout"]]])

(defn home-page []
  [:div.ui.container
   [:h1.ui.header "Home Page"]])

(def pages
  {:home #'home-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (rf/dispatch [:set-active-page :home]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app

(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-components))
