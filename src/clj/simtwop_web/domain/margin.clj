(ns simtwop-web.domain.margin
  (:gen-class) )

(def margins {:principal 66.3 :lead 68.0 :senior 73.4 :con 76.1 :grad 79.0})

(defn margin-calc [expected-roles actual-roles]
	(let [{expected-grade :grade} expected-roles {actual-grade :grade} actual-roles]
	 	(or (actual-grade margins) 0)))