(ns simtwop-web.test.domain.people-test
  	(:require 	
			[clojure.test :refer :all]
      [simtwop-web.domain.people :refer :all]
      ))

(deftest ps-frequency-calculation
  (testing "counts each grade/role cohort"
    (are [x y] (= x (ps-frequencies y))
      '{(:con :qa) 1}                 '({:grade :con :role :qa}) 
      '{}                             '() 
      '{(:con :qa) 1 (:lead :dev) 1}  '({:grade :con :role :qa} {:grade :lead :role :dev}))
      '{(:con :qa) 2}                 '({:grade :con :role :qa} {:grade :con :role :qa})
      '{(:con :qa) 2 (:lead :dev) 1}  '({:grade :con :role :qa} {:grade :con :role :qa} {:grade :lead :role :dev})
      ))

(deftest ps-population
  (testing "populates the specified number of ps"
    (are [x y] (= x (count y))
    	1 (ps-populate 1)
    	0 (ps-populate 0)
    	40 (ps-populate 40)))

  (testing "populates a series of ps role/grade tuples"
    (let [ps-tuple (first (ps-populate 1))]
      (is (not (nil? (:role ps-tuple))))
      (is (not (nil? (:grade ps-tuple)))))))
