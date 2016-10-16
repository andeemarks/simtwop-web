(ns simtwop-web.domain.staffing
  (:require 
  	[clj-time.core :as t]			
  	[aprint.core :refer :all] )
  (:gen-class))

(defn fill [project available-staff]
	(let [spots-to-fill (project :spots)
				filled-spots (clojure.set/intersection (set spots-to-fill) (set available-staff))
				all-spots-filled? (= (count filled-spots) (count spots-to-fill))
				some-spots-filled? (< 0 (count filled-spots) (count spots-to-fill))]
		(cond 
			some-spots-filled? {:status :partial :filled_spots filled-spots}
			all-spots-filled? {:status :pass :filled_spots filled-spots}
			:else {:status :fail :filled_spots filled-spots})))
