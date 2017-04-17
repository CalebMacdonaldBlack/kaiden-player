(ns kaiden-player.views.test-route-template
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [kaiden-player.views.root-template :refer [nav-link]]
            [re-frame.core :as rf]))

(defn- mock-subscribe
  [coll]
  (case (first coll)
    :page (atom :home)))


(deftest test-nav-link
  (testing "nav-link"
    (with-redefs [rf/subscribe mock-subscribe]
      (let [expected [:a.item {:class "active", :href "#/"} "Home"]
            actual (nav-link "#/" "Home" :home)]
        (is (= actual expected))))))
