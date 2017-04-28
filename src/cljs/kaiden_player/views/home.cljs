(ns kaiden-player.views.home
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [ajax.core :refer [GET]]))

(defonce url (r/atom ""))

(def mp3-api-endpoint "http://www.youtubeinmp3.com/fetch/?format=json&filesize=1&video=")

(defn remove-mp3-suffix [song-name]
  (string/replace song-name ".mp3" ""))

(defn- youtubeinmp3-handler [url]
  (fn [response]
      (reset! url "")
      (rf/dispatch [:add-song response])))

(defn- youtubeinmp3-error-handler [response]
  (rf/dispatch [:song-not-found response]))

(defn- url-input [loading url]
  [:input {:type        "text"
           :name        "url"
           :disabled    @loading
           :placeholder "Youtube song URL"
           :value       @url
           :on-change   #(reset! url (.-target.value %))}])

(defn- submit-button [loading url endpoint]
  [:button.ui.button {:type     "button"
                      :disabled @loading
                      :on-click #(GET (str endpoint (js/encodeURIComponent @url))
                                      {:handler         (youtubeinmp3-handler url)
                                       :error-handler   youtubeinmp3-error-handler
                                       :response-format :json})}
   "Add"])

(defn- message [loading error-msg success-msg]
  [:div
   (when @loading
     [:img {:src "/img/loading.gif" :style {:display "block" :margin "auto"}}])
   (when @error-msg
     [:small {:style {:color "red"}} @error-msg])
   (when @success-msg
     [:small {:style {:color "green"}} @success-msg])])

(defn list-songs []
  [:table.ui.celled.striped.table.inverted.selectable
   [:thead
    [:tr
     [:th "Songs"]]]
   [:tbody
    (for [song @(rf/subscribe [:rotated-songs])]
      [:tr
       [:td {:on-click #(do (rf/dispatch [:set-current-song song]))}
            (remove-mp3-suffix song)]])]])

(defn shuffle-button []
  [:button.ui.button {:on-click #(rf/dispatch [:shuffle-songs])} "Shuffle"])
  
(defn music-player []
  (let [current-song @(rf/subscribe [:current-song])]
    [:audio#player {:controls true
                    :on-ended #(rf/dispatch [:next-song])
                    :on-loaded-data #(.play (.getElementById js/document "player"))
                    :on-play #(rf/dispatch [:set-music-playing true])
                    :on-pause #(rf/dispatch [:set-music-playing false])}
     [:source#player-source {:type "audio/mpeg"}]]))


(defn home-page []
  [:div.ui.grid
   [:div.four.wide.column]
   (let [error-msg (rf/subscribe [:error-msg])
         success-msg (rf/subscribe [:success-msg])
         loading (rf/subscribe [:loading])]
     [:div.eight.wide.column
      [:form.ui.form
       [:h1.ui.center.aligned.header.inverted "Add Song to Playlist"]
       [:div.ui.action.input {:style {:width "90%"}}
        (url-input loading url)
        (submit-button loading url mp3-api-endpoint)]]
      (message loading error-msg success-msg)])
   [:div.four.wide.column]
   [:div.two.wide.column]
   [:div.twelve.wide.column
    [:h1.ui.header.inverted "Player"]
    (music-player)
    [:br]
    [:br]
    (shuffle-button)
    [:button.ui.button {:on-click #(rf/dispatch [:next-song])} "Skip"]
    [:br]
    [:br]
    (when @(rf/subscribe [:music-playing])
      [:img {:src @(rf/subscribe [:dancing-gif]) :style {:borderRadius "1em"}}])
    (let [current-song @(rf/subscribe [:current-song])]
      (when current-song
        [:h2.ui.header.inverted "Currently playing: " [:em [:small {:style {:color "#aaa" :font-weight "100"}} (remove-mp3-suffix @(rf/subscribe [:current-song]))]]]))
    (list-songs)]
   [:div.two.wide.column]])
