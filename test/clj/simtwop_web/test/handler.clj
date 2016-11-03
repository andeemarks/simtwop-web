(ns simtwop-web.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [simtwop-web.handler :refer :all]))

(deftest test-app
  (testing "requesting a generation"
    (let [response ((app) (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "posting a generation"
  	(let [response ((app) (request :post "/1"))]
			(is (= 302 (:status response))))))
