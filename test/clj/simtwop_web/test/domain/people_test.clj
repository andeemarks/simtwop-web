(ns simtwop-web.test.domain.people-test
  	(:require 	
			[clojure.test :refer :all]
      [simtwop-web.domain.people :refer :all]
      ))

(deftest ps-population
  (testing "populates the specified number of ps"
    (are [x y] (= x (count y))
    	1 (ps-populate 1)
    	0 (ps-populate 0)
    	40 (ps-populate 40)))

  (testing "populates a series of ps, each with a role and grade"
  	(is (not (nil? (:role (first (ps-populate 1))))))
  	(is (not (nil? (:grade (first (ps-populate 1))))))))
