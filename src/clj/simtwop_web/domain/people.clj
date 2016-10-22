(ns simtwop-web.domain.people
  (:gen-class) )

(def ps-role-distribution [:dev :dev :dev :dev :dev :dev :ba :qa :pm :ux :specialist])
(def ps-grade-distribution [:grad :con :con :con :senior :senior :senior :lead :lead :principal])

(defn- build-ps []
	{:role (rand-nth ps-role-distribution) :grade (rand-nth ps-grade-distribution)})

(defn ps-populate [ps-size]
	(repeatedly ps-size #(build-ps)))

(defn ps-frequencies [ps-population]
	(frequencies (map vals ps-population)))
