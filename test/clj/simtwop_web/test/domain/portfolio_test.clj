(ns simtwop-web.test.domain.portfolio-test
  	(:require 	
      [clj-time.core :as t]
			[aprint.core :refer :all]	
			[clojure.test :refer :all]
    	[simtwop-web.domain.portfolio :refer :all]
      ))

(deftest assignment-updates
  (testing "a single assignment can be added in a project"
    (let [project (assoc (dissoc (demand-generate) :spots) :spots '[{:grade "lead", :id "lead_ux_1", :role "ux"}])
          assignments {:role-lead_ux_1 "lead_ux"}
          updated-project (update-assignments project assignments)]
      (is (= "lead_ux" (:assigned (first (:spots updated-project)))))))

  (testing "unfound assignment result in no change to project"
    (let [project (assoc (dissoc (demand-generate) :spots) :spots '[{:grade "lead", :id "lead_ux_1", :role "ux"}])
          assignments {:role-lead_ux_2 "lead_dev"}
          updated-project (update-assignments project assignments)]
      (is (nil? (:assigned (first (:spots updated-project)))))))

  (testing "multiple assignments can be added to a project"
    (let [project (assoc (dissoc (demand-generate) :spots) :spots '[{:grade "lead", :id "lead_ux_1", :role "ux"} {:grade "con", :id "con_dev_1", :role "dev"}])
          assignments {:role-lead_ux_1 "lead_ux", :role-con_dev_1 "senior_dev"}
          updated-project (update-assignments project assignments)]
      (is (= "lead_ux" (:assigned (first (:spots updated-project)))))
      (is (= "senior_dev" (:assigned (second (:spots updated-project))))))))

(deftest demand-generation
  (testing "generates a project with start and end dates, duration and delay"
	  (is (not (nil? (:end-date (demand-generate)))))
    (is (not (nil? (:duration-weeks (demand-generate)))))
    (is (not (nil? (:delay-weeks (demand-generate)))))
  	(is (not (nil? (:start-date (demand-generate))))))

  (testing "generates a project with a set of spot requests"
    (let [spots (:spots (demand-generate))]
      (is (not (nil? spots)))
      (is (< 0 (count spots)))))

  (testing "generates a project with a description"
      (is (not (nil? (:type (demand-generate))))))

	(testing "end dates succeed start dates"
		(let [project (demand-generate)]
			(is (t/after? (project :end-date) (project :start-date))))))
