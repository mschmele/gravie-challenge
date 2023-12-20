(ns the-bomb-dot-com.checkout
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]))

(defonce cart (atom ()))
(defonce rented-games (atom ()))

(defn add-to-cart! [item]
  (swap! cart conj item))