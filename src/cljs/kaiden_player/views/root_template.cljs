(ns kaiden-player.views.root-template
  (:require [re-frame.core :as rf]
            [kaiden-player.views.home :refer [home-page]]))

(def pages
  {:home #'home-page})

(defn nav-link [uri title page]
  (let [selected-page (rf/subscribe [:page])]
    [:a.item
     {:class (when (= page @selected-page) "active")
      :href uri}
     title]))

(defn navbar []
  [:div.ui.menu.inverted
   [:div.header.item "Kaiden Player"]
   (nav-link "#/" "Home" :home)
   [:div.right.menu
    [:a.item {:href "/logout"} "Logout"]]])

(defn page []
  [:div {:style {:background-color "#3D3D3D"}}
   [navbar]
   [(pages @(rf/subscribe [:page]))]])
