(ns the-bomb-dot-com.prod
  (:require [the-bomb-dot-com.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
