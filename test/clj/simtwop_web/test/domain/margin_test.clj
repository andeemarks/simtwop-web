(ns simtwop-web.test.domain.margin-test
  	(:require 	
      [clj-time.core :as t]
			[aprint.core :refer :all]	
			[clojure.test :refer :all]
    	[simtwop-web.domain.margin :refer :all]
      ))

(deftest margin-calculation
  (testing "margins are zero with unknown grade"
    (is (= 0 (margin-calc {:grade :principoo} {:grade :principoo}))))
  
  (testing "margins decrease with higher grades"
    (is (< (margin-calc {:grade :principal} {:grade :principal})
            (margin-calc {:grade :lead} {:grade :lead})))
    (is (< (margin-calc {:grade :lead} {:grade :lead})
            (margin-calc {:grade :senior} {:grade :senior})))
    (is (< (margin-calc {:grade :senior} {:grade :senior})
            (margin-calc {:grade :con} {:grade :con})))
    (is (< (margin-calc {:grade :con} {:grade :con})
            (margin-calc {:grade :grad} {:grade :grad})))
    ))