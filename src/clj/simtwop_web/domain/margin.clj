(ns simtwop-web.domain.margin
  (:gen-class)
	(:require 	
		[aprint.core :refer :all] ))

(def margins {:principal 66.3 :lead 68.0 :senior 73.4 :con 76.1 :grad 79.0})

(defn- margin-lookup [actual-grade]
 	(or (actual-grade margins) 0))

(defn margin-calc [grade-tuples]
	(let [all-margins (map #(margin-lookup %1) grade-tuples)]
		(/ (apply + all-margins) (count all-margins))))