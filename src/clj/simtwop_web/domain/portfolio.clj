(ns simtwop-web.domain.portfolio
  (:require [clj-time.core :as t])
  (:gen-class))

(defn- spot 
	([grade role] (spot grade role 1))
	([grade role index] {:role (name role) :grade (name grade) :id (str (name grade) "_" (name role) "_" index)}))

(def project-start-delay-distribution-weeks [1 2 3 4])

(defrecord ProjectTemplate [type length-distribution-weeks spots])

(defmulti project-picker identity)
(defmethod project-picker :discovery [_] (ProjectTemplate. 
					"Discovery" 
					[1 2]
					[	(spot :lead :ux) 
						(spot :senior :ba) 
						(spot :lead :dev)]))

(defmethod project-picker :inception [_] (ProjectTemplate. 
					"Inception" 
					[1 2]
					[	(spot :lead :ux) 
						(spot :senior :ba) 
						(spot :lead :dev) 
						(spot :principal :pm)]))

(defmethod project-picker :assessment [_] (ProjectTemplate. 
					"Assessment" 
					[1 2 2 3 4 4]
					[	(spot :principal :dev)
						(spot :lead :dev)
						(spot :senior :dev) ]))

(defmethod project-picker :small-delivery [_] (ProjectTemplate. 
					"Small Delivery Project" 
					[8 12 16 24]
					[
						(spot :con :qa) 
						(spot :senior :ba) 
						(spot :grad :dev) 
						(spot :con :dev) 
						(spot :senior :dev)]))

(defmethod project-picker :medium-delivery [_] (ProjectTemplate. 
					"Medium Delivery Project" 
					[12 16 16 24 24 30 30]
					[
						(spot :con :qa) 
						(spot :senior :ba) 
						(spot :grad :dev) 
						(spot :con :dev) 
						(spot :con :dev 2) 
						(spot :lead :dev) 
						(spot :senior :dev)]))

(defmethod project-picker :large-delivery [_] (ProjectTemplate. 
					"Large Delivery Project" 
					[16 16 24 24 30 30 36 36]
					[
						(spot :senior :qa) 
						(spot :lead :ba) 
						(spot :lead :pm)
						(spot :grad :dev) 
						(spot :grad :dev 2) 
						(spot :con :dev) 
						(spot :con :dev 2) 
						(spot :con :dev 3) 
						(spot :lead :dev) 
						(spot :senior :dev)]))

(defmethod project-picker :staff-aug [_] (ProjectTemplate. 
					"Staff Augmentation" 
					[16 16 24 24 30 30 36 36]
					[	(spot :con :dev)
						(spot :con :dev 2) 
						(spot :con :dev 3) 
						(spot :senior :dev)]))

(def project-type-distribution [
	:discovery :discovery :discovery :discovery :discovery 
	:inception :inception :inception :inception :inception 
	:small-delivery :small-delivery 
	:medium-delivery 
	:large-delivery 
	:staff-aug :staff-aug :staff-aug 
	:assessment :assessment :assessment])

(defn- generate-project []
	(project-picker (rand-nth project-type-distribution)))

(defn demand-generate []
	(let [now (t/now)
			project (generate-project)
			duration (rand-nth (:length-distribution-weeks project))
			delay (rand-nth project-start-delay-distribution-weeks)
			start (t/plus now (t/weeks delay))
			end (t/plus start (t/weeks duration))]
		{:start-date start :duration-weeks duration :end-date end :delay-weeks delay :type (:type project) :spots (:spots project)}))

(defn update-assignments [project assignments]
	project)
