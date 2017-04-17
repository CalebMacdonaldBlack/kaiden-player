(ns kaiden-player.views.test-route-template
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [kaiden-player.views.root-template :refer [nav-link pages navbar page]]
            [re-frame.core :as rf]))

(defn- mock-page
  []
  [:h1 "test page"])

(defn- mock-subscribe
  [coll]
  (case (first coll)
    :page (atom :home)))

(defn- mock-subscribe2
  [coll]
  (case (first coll)
    :page (atom :other)))

(deftest test-root-template
  (testing "nav-link with active link"
    (with-redefs [rf/subscribe mock-subscribe]
                 (let [expected [:a.item {:class "active", :href "#/"} "Home"]
                       actual (nav-link "#/" "Home" :home)]
                   (is (= actual expected)))))

  (testing "nav-link without active link"
    (with-redefs [rf/subscribe mock-subscribe2]
                 (let [expected [:a.item {:class nil, :href "#/"} "Home"]
                       actual (nav-link "#/" "Home" :home)]
                   (is (= actual expected)))))

  (testing "navbar"
    (with-redefs [rf/subscribe mock-subscribe]
                 (let [expected [:div.ui.menu
                                 [:div.header.item "Kaiden Player"]
                                 [:a.item {:class "active", :href "#/"} "Home"]
                                 [:div.right.menu [:a.item {:href "/logout"} "Logout"]]]
                       actual (navbar)]
                   (is (= expected actual)))))
  (testing "page"
    (with-redefs [pages {:home #'mock-page}
                  navbar [:nav]
                  rf/subscribe mock-subscribe]
                 (let [expected [:div
                                 [[:nav]]
                                 [#'kaiden-player.views.test-route-template/mock-page]]
                       actual (page)]
                   (is (= expected actual))))))

