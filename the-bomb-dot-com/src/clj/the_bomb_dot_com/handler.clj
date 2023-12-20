(ns the-bomb-dot-com.handler
  (:require
   [reitit.ring :as reitit-ring]
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [the-bomb-dot-com.middleware :refer [middleware]]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]))

(def mount-target
  [:div#app
   [:h2 "Welcome to the-bomb-dot-com"]
   [:p "please wait while Figwheel/shadow-cljs is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")]))

(defn index-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (loading-page)})

(defn- pick-game-fields [game]
  (let[{:keys [id image name aliases site_detail_url]} game]
   {:id id
    :name name
    :aliases aliases
    :icon (:icon_url image)
    :gb-link site_detail_url}))

(defn find-games
  [request]
  (let [url "https://www.giantbomb.com/api/search/"
        title (-> request :params :game)
        response (client/get url {:headers {"User-Agent" "Mozilla/5.0 (Windows NT 6.1;) Gecko/20100101 Firefox/13.0.1"}
                                  :query-params {:api_key "0059d8dde6ea8709ced1aa7fe54fe98e727891c4"
                                                 :format "json"
                                                 :query title
                                                 :resources "game"}})
        body (-> response
                 :body
                 (json/read-str :key-fn keyword)
                 :results)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (->> body 
                (map pick-game-fields)
                json/write-str)}))

(def app
  (reitit-ring/ring-handler
   (reitit-ring/router
    [["/" {:get {:handler index-handler}}]
     ["/items"
      ["" {:get {:handler index-handler}}]
      ["/:item-id" {:get {:handler index-handler
                          :parameters {:path {:item-id int?}}}}]]
     ["/about" {:get {:handler index-handler}}]
     ["/find-games" {:get {:handler find-games
                          :parameters {:path {:game-title string?}}}}]])
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/" :root "/public"})
    (reitit-ring/create-default-handler))
   {:middleware middleware}))
