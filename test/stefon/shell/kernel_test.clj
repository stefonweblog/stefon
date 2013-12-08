(ns stefon.shell.kernel-test
  (:use clojure.test
        midje.sweet))

(deftest test-app

  (testing "main route"

    (is (= 0 0))
    (is (= 5 5)))

)
