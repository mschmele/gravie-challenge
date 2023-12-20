(ns the-bomb-dot-com.search
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :refer [<! go]]
   [reagent.core :as reagent :refer [atom]]
   [the-bomb-dot-com.checkout :as checkout]))

(defonce results (atom ()))

(defn- find-game-by-title [title]
  (go
    (let [query (<! (http/get "http://localhost:3000/find-games"
                              {:query-params {:game title}}))] 
      (reset! results (:body query)))))

(defn- display-results []
  (let [results (map (fn [{:keys [id name aliases :gb-link]}]
                           [:ul 
                            [:li (str "Title: " name)]
                            [:li (str "ID: " id)]
                            (when (seq aliases)
                             [:li (str "Aliases: " aliases)])
                            [:li (str "More information: " gb-link)]
                            [:input {:type "button"
                                     :value "add to cart"
                                     :on-click #(checkout/add-to-cart! {:id id :name name})}]])
                         @results)]
    [:ul results]))

(defn render [_routing-data]
  [:span.main
   [:h1 (str "Let's find a game, shall we?")]
   [:input {:type "text"
            :placeholder "search by title"
            :on-key-down (fn [e]
                           (when (= "Enter" (.-key e))
                             (find-game-by-title (.. e -target -value))))}]
   [:div (display-results)]])

(comment (tap> (go (<! (http/get "http://localhost:3000/find-games"
                                 {:query-params {:game "metroid prime"}}))))
         (println *e))