(ns simtwop-web.test.domain.margin-test
  	(:require 	
      [clj-time.core :as t]
			[aprint.core :refer :all]	
			[clojure.test :refer :all]
      [clojure.algo.generic.math-functions :refer :all]
    	[simtwop-web.domain.margin :refer :all]
      ))

(deftest margin-calculation
  (testing "margins are zero with unknown grade"
    (is (= 0 (margin-calc [:principoo]))))

  (testing "margins average out across all roles"
    (let [con-margin (margin-calc '[:con])
          snr-margin (margin-calc '[:senior])]
      (is (approx= 
        (/ (+ con-margin snr-margin) 2) 
        (margin-calc [:con :senior])
        0.0001))))

  (testing "margins decrease with higher grades"
    (is (< (margin-calc [:principal])
            (margin-calc [:lead])))
    (is (< (margin-calc [:lead])
            (margin-calc [:senior])))
    (is (< (margin-calc [:senior])
            (margin-calc [:con])))
    (is (< (margin-calc [:con])
            (margin-calc [:grad])))
    ))