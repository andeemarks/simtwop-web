(ns simtwop-web.test.domain.staffing-test
  	(:require 	
      [clj-time.core :as t]
			[aprint.core :refer :all]	
			[clojure.test :refer :all]
      [simtwop-web.domain.portfolio :refer :all]
    	[simtwop-web.domain.staffing :as fut]
      ))

(deftest filling-staffing-requests
  (testing "fails with no availability"
    (let [project (demand-generate)
          result (fut/fill project [])]
      (is (= 0 (count (result :filled_spots))))
      (is (= :fail (result :status)))))

  (testing "fails with right number, wrong role"
    (let [dummy-spot {:grade :con, :role :qa}
          dummy-project {:spots [{:grade :con, :role :dev}]}
          result (fut/fill dummy-project [dummy-spot])]
      (is (= 0 (count (result :filled_spots))))
      (is (= :fail (result :status)))))

  (testing "partially succeeds when at least one spot can be filled"
    (let [dummy-spots [{:grade :con :role :qa} {:grade :lead :role :ux}]
          dummy-project {:spots dummy-spots}
          result (fut/fill dummy-project [{:grade :con, :role :qa}])]
      (is (= 1 (count (result :filled_spots))))
      (is (= :partial (result :status)))))

  ; (testing "partially succeeds when at a spot can be filled by the same role at a higher grade"
  ;   (let [dummy-spots [{:grade :con :role :qa}]
  ;         dummy-project {:spots dummy-spots}
  ;         result (fut/fill dummy-project [{:grade :senior, :role :qa}])]
  ;     (is (= 1 (count (result :filled_spots))))
  ;     (is (= :partial (result :status)))))

  (testing "succeeds with an exact match"
    (let [dummy-spot {:grade :con, :role :qa}
          dummy-project {:spots [dummy-spot]}
          result (fut/fill dummy-project [dummy-spot])]
      (is (= 1 (count (result :filled_spots))))
      (is (= :pass (result :status)))))

  )
