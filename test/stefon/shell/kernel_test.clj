(ns stefon.shell.kernel-test
  (:use clojure.test
        midje.sweet)
  (:require stefon.core
            [stefon.shell.kernel :as kernel]
            [heartbeat.plugin :as heartbeat]))


(defn- system-shape []
  {:domain {:posts [], :assets [], :tags []},
   :channel-list [],
   :send-fns [],
   :recieve-fns [],
   :tee-fns []})

(defn- matches-domain-shape [input-shape]
  (= input-shape (system-shape)))

(defn- matches-system-shape [input-shape]
  (= input-shape {:stefon/system (system-shape)}))

(deftest test-app

  ;; Create & Manage the System
  (testing "System generation"
    (let [kernel-result (kernel/generate-system)]
      (is (not (nil? kernel-result)))
      (is (map? kernel-result))
      (is (matches-domain-shape kernel-result))))

  (testing "Kernel Startup"
    (let [start-result (kernel/start-system)]
      (is (not (nil? start-result)))
      (is (map? start-result))
      (is (matches-system-shape start-result))

      (let [system-state (kernel/get-system)]

        (is (not (nil? system-state)))
        (is (map? system-state))
        (is (matches-system-shape system-state)))))


  ;; Commuincating with Plugins
  (testing "We can get the Plugin's plugin function"
    (let [plugin-fn (kernel/get-plugin-fn 'heartbeat.plugin)]
      (is (fn? @plugin-fn))))

  (testing "We can invoke the Plugin's plugin function"
    (let [plugin-receive (kernel/invoke-plugin-fn 'heartbeat.plugin)]
      (is (fn? plugin-receive))))

  (testing "Handshake 1: Invoke plugin's (plugin) function"

    (let [plugin-receive (kernel/load-plugin 'heartbeat.plugin)]

      )

    ;; Handshake 2: Return channel and send & receive functions

    ;; Plugin requests Schema - deliver

    ;; Plugin sends heartbeat - deliver

    ;; Plugin adds a post - deliver

    ;; Plugin adds a post; Second Plugin also responds - deliver

    )
  #_(testing "Handshake 2: Return channel and send & receive functions"

    (let [plugin-receive (kernel/load-plugin 'heartbeat.plugin)
          plugin-ackack (kernel/plugin-handshake-ack 'heartbeat.plugin)]

      )

    )
  )
