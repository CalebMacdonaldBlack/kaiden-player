(ns kaiden-player.views.home
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [ajax.core :refer [GET]]))

(defonce url (r/atom ""))

(def mp3-api-endpoint "http://www.youtubeinmp3.com/fetch/?format=json&filesize=1&video=")

(defn youtubeinmp3-handler [response]
  (reset! url "")
  (rf/dispatch [:add-song response]))

(defn youtubeinmp3-error-handler [response]
  (rf/dispatch [:song-not-found response]))

(defn home-page []
  [:div.ui.grid
   [:div.four.wide.column]
   (let [error-msg (rf/subscribe [:error-msg])
         success-msg (rf/subscribe [:success-msg])
         loading (rf/subscribe [:loading])]
     [:div.eight.wide.column
        [:form.ui.form
         [:h1.ui.center.aligned.header "Add Song to Playlist"]
         [:div.ui.action.input {:style {:width "90%"}}
          [:input {:type "text"
                   :name "url"
                   :disabled @loading
                   :placeholder "Youtube song URL"
                   :value @url
                   :on-change #(reset! url (.-target.value %))}]
          [:button.ui.button {:type "button"
                              :disabled @loading
                              :on-click #(GET (str mp3-api-endpoint (js/encodeURIComponent @url))
                                              {:handler youtubeinmp3-handler
                                               :error-handler youtubeinmp3-error-handler
                                               :response-format :json})}
                             "Add"]]]
        (when @loading
          [:img {:src "/img/loading.gif" :style {:display "block" :margin "auto"}}])
        (when @error-msg
          [:small {:style {:color "red"}} @error-msg])
        (when @success-msg
          [:small {:style {:color "green"}} @success-msg])
        [:audio {:controls true}
         [:source {:src "/test" type "audio/mpeg"}]]])
   [:div.four.wide.column]])
