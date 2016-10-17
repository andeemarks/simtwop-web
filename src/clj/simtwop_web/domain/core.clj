(ns simtwop-web.domain.core
  (:gen-class)
  (:require [aprint.core :refer :all])
  (:require [simtwop-web.domain.portfolio :as portfolio])
  (:require [simtwop-web.domain.people :as ps])
  (:require [clj-time.core :as t])
  (:require [clj-time.format :as f])
  (:require [clj-time.periodic :as p])
  (:require [clj-time.predicates :as pred])
  (:require [hiccup.core :as h])
  )

(defn- format-role-description [role-name grade]
	[:td {:class (str "staffing-plans-role-cell open_role_background_" role-name)} 
		[:div {:class "role-description"} 
			[:em role-name]
			[:br]]])

(defn- format-open-role [project-length role]
	(let [role-name (name (role :role))]
		[:tr {:class "staffing-plans-role"}
			(format-role-description role-name (role :grade))
			(for [week (range 0 project-length)]
				[:td {:class (str "open_role_background_" role-name)}])]))	

(defn- format-open-roles [roles project-length]
	(map #(format-open-role project-length %1) roles))

(defn- extract-key-date-elements [date]
	{:month (f/unparse (f/formatter "MMM") date) :start-of-week (f/unparse (f/formatter "d") date)})

(defn generate-date-stream [start-date end-date]
	(let [days-leading-to-start-date (take 7 (p/periodic-seq start-date (t/days -1)))
				first-day-of-first-week (first (filter pred/monday? days-leading-to-start-date))
				number-of-weeks (+ 1 (t/in-weeks (t/interval start-date end-date)))
				first-days-of-each-week (take number-of-weeks (p/periodic-seq first-day-of-first-week (t/weeks 1)))]
		(map extract-key-date-elements first-days-of-each-week)))
