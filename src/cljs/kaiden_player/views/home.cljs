(ns kaiden-player.views.home)

(defn home-page []
  [:div.ui.container
   [:h1.ui.header "Home Page"]]
  [:div.ui.grid
   [:div.four.wide.column]
   [:div.eight.wide.column
    [:form.ui.form
     [:h1.ui.center.aligned.header "Add Song to Playlist"]
     [:div.ui.action.input {:style {:width "90%"}}
      [:input {:type "text" :name "url" :placeholder "Youtube song URL"}]
      [:button.ui.button "Add"]]]]
   [:div.four.wide.column]])
