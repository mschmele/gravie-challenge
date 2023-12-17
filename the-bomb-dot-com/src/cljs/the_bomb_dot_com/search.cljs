(ns the-bomb-dot-com.search
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]))

(defonce search-term (atom ""))
(defonce results (atom ()))

(defn- find-game-by-title [title]
  (println (str "input: " title))
  (reset! results [{:title (str title " soup IV") :genre "soup"}
                   {:title (str title " soup XVII") :genre "soup 2"}])
  
  (println @results))

(defn- display-results []
  (let [please-work (map (fn [result]
                           [:li (str "Title: " (:title result) " | genre: " (:genre result))])
                         @results)]
    [:ul please-work]))

(defn render [_routing-data]
  [:span.main
   [:h1 (str "Let's find a game, shall we?")]
   [:input {:type "text"
            :placeholder "search by title"
            :on-key-down (fn [e]
                           (when (= "Enter" (.-key e))
                             (reset! search-term (.. e -target -value))
                             (find-game-by-title @search-term)))}]
   [:div (display-results)]])