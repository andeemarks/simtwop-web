(ns simtwop-web.domain.people
  (:gen-class) )

(def ps-role-distribution [:dev :dev :dev :dev :dev :dev :ba :qa :pm :ux :specialist])
(def ps-grade-distribution [:grad :con :con :con :senior :senior :senior :lead :lead :principal])

(defn- build-ps []
	{:role (rand-nth ps-role-distribution) :grade (rand-nth ps-grade-distribution)})

(defn- build-ps-raw []
	{(rand-nth ps-role-distribution) (rand-nth ps-grade-distribution)})

(defn ps-populate [ps-size]
	(repeatedly ps-size #(build-ps-raw)))

(defn initial-population []
	'{:beach-con_ba "4", :beach-con_dev "8", :beach-con_pm "4", :beach-con_qa "4", :beach-con_specialist "1", :beach-con_ux "4", 
		:beach-grad_ba "2", :beach-grad_dev "7", :beach-grad_pm "0", :beach-grad_qa "3", :beach-grad_specialist "2", :beach-grad_ux "0", 
		:beach-lead_ba "3", :beach-lead_dev "10", :beach-lead_pm "3", :beach-lead_qa "2", :beach-lead_specialist "3", :beach-lead_ux "2", 
		:beach-principal_ba "0", :beach-principal_dev "6", :beach-principal_pm "2", :beach-principal_qa "2", :beach-principal_specialist "0", :beach-principal_ux "1", 
		:beach-senior_ba "2", :beach-senior_dev "18", :beach-senior_pm "2", :beach-senior_qa "3", :beach-senior_specialist "1", :beach-senior_ux "1"})

(defn ps-frequencies [ps-population]
	(frequencies (map vals ps-population)))
