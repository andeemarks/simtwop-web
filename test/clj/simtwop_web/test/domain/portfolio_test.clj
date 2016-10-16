(ns simtwop-web.test.domain.portfolio-test
  	(:require 	
      [clj-time.core :as t]
			[aprint.core :refer :all]	
			[clojure.test :refer :all]
    	[simtwop-web.domain.portfolio :refer :all]
      ))

(deftest demand-generation
  (testing "generates a project with start and end dates, duration and delay"
	  (is (not (nil? (:end-date (demand-generate)))))
    (is (not (nil? (:duration-weeks (demand-generate)))))
    (is (not (nil? (:delay-weeks (demand-generate)))))
  	(is (not (nil? (:start-date (demand-generate))))))

  (testing "generates a project with a set of spots"
    (let [spots (:spots (demand-generate))]
      (is (not (nil? spots)))
      (is (< 0 (count spots)))))

  (testing "generates a project with a description"
      (is (not (nil? (:type (demand-generate))))))

	(testing "end dates succeed start dates"
		(let [project (demand-generate)]
			(is (t/after? (project :end-date) (project :start-date))))))
