(ns stefon.shell.kernel-test
  (:use clojure.test
        midje.sweet)
  (:require stefon.core
            [stefon.shell.kernel :as kernel]))


(defn- create-lifecycle []
  (stefon.core.Component. nil))

(defn- matches-system-shape [input-shape]
  (= input-shape
     {:domain {:posts [], :assets [], :tags []},
      :channel-list [],
      :send-fns [],
      :recieve-fns [],
      :tee-fns []}))

(deftest test-app

  ;; Create & Manage the System
  (testing "System generation"
    (let [kernel-result (kernel/generate-system)]
      (is (not (nil? kernel-result)))
      (is (map? kernel-result))
      (is (matches-system-shape kernel-result))))

  (testing "Kernel Startup"
    (let [system-result (kernel/generate-system)
          start-result (kernel/start-system system-result)]
      (is (not (nil? start-result)))
      (is (map? start-result))
      (is (matches-system-shape start-result))

      (let [system-state (kernel/get-system)]
        (is (not (nil? system-state)))
        (is (map? system-state))
        (is (matches-system-shape system-state)))))


  ;; Commuincating with Plugins
  (testing "Communicating with Plugins"

    )
  )
