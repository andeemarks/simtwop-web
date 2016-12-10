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
    (is (= 0 (margin-calc [{:grade :principoo}]))))

  (testing "margins average out across all roles"
    (let [con-margin (margin-calc '[{:grade :con}])
          snr-margin (margin-calc '[{:grade :senior}])]
      (is (approx= 
        (/ 2 (+ con-margin snr-margin)) 
        (margin-calc [{:grade :con} {:grade :senior}])
        0.1))))

  (testing "margins decrease with higher grades"
    (is (< (margin-calc [{:grade :principal}])
            (margin-calc [{:grade :lead}])))
    (is (< (margin-calc [{:grade :lead}])
            (margin-calc [{:grade :senior}])))
    (is (< (margin-calc [{:grade :senior}])
            (margin-calc [{:grade :con}])))
    (is (< (margin-calc [{:grade :con}])
            (margin-calc [{:grade :grad}])))
    ))