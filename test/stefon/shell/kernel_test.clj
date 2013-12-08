(ns stefon.shell.kernel-test
  (:use clojure.test
        midje.sweet)
  (:require [stefon.shell.kernel :as kernel]))

(deftest test-app

  ;; Create & Manage the System
  (testing "System generation"

    (is (not (nil? (kernel/generate-system)))))


  ;; Commuincating with Plugins
  (testing "Communicating with Plugins"

    )
  )
