(ns kaiden-player.views.home
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [ajax.core :refer [GET]]))

(defonce url (r/atom ""))

(def mp3-api-endpoint "http://www.youtubeinmp3.com/fetch/?format=json&video=")

(defn youtubeinmp3-handler [response]
  (rf/dispatch [:add-song response]))

(defn youtubeinmp3-error-handler [response]
  (rf/dispatch [:song-not-found response]))

(defn home-page []
  [:div.ui.grid
   [:div.four.wide.column]
   (let [error-msg (rf/subscribe [:error-msg])
         success-msg (rf/subscribe [:success-msg])]
     [:div.eight.wide.column
        [:form.ui.form
         [:h1.ui.center.aligned.header "Add Song to Playlist"]
         [:div.ui.action.input {:style {:width "90%"}}
          [:input {:type "text"
                   :name "url"
                   :placeholder "Youtube song URL"
                   :on-change #(reset! url (.-target.value %))}]
          [:button.ui.button {:type "button"
                              :on-click #(GET (str mp3-api-endpoint (js/encodeURIComponent @url))
                                              {:handler youtubeinmp3-handler
                                               :error-handler youtubeinmp3-error-handler
                                               :response-format :json})}
                             "Add"]]]
        (when @error-msg
          [:small {:style {:color "red"}} @error-msg])
        (when @success-msg
          [:small {:style {:color "green"}} @success-msg])])
   [:div.four.wide.column]])
