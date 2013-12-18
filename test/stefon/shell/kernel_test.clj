(ns stefon.shell.kernel-test
  (:use clojure.test
        midje.sweet)
  (:require [clojure.core.async :as async :refer :all]
            [stefon.core :as core]
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

(deftest test-kernel

  ;; Create & Manage the System
  (testing "System generation"
    (let [kernel-result (kernel/generate-system)]
      (is (not (nil? kernel-result)))
      (is (map? kernel-result))
      (is (matches-domain-shape kernel-result))))

  (testing "Kernel Startup"
    (let [start-result (kernel/start-system)]

      (is (not (nil? @start-result)))
      (is (map? @start-result))
      (is (matches-system-shape (dissoc @start-result :channel-list)))

      (let [system-state (kernel/get-system)]

        (is (not (nil? @system-state)))
        (is (map? @system-state))
        (is (matches-system-shape (dissoc @system-state :channel-list))))))

  (testing "Should already have a core channel-list "

      (let [xx (kernel/start-system)]

        (is (not (nil? (:channel-list @kernel/*SYSTEM*))))
        (is (vector? (:channel-list @kernel/*SYSTEM*)))))


  (testing "Should be able to add channels to a list"

      (let [new-channel (chan)

            xx (kernel/start-system)

            add-result (try
                         (kernel/add-to-channel-list new-channel)
                         (catch Exception e e))
            add-result-2 (try
                           (kernel/add-to-channel-list {:id 2 :channel new-channel})
                           (catch Exception e e))
            add-result-3 (try
                           (kernel/add-to-channel-list {:id "ID" :channel new-channel})
                           (catch Exception e e))]

        (is (not (nil? add-result)))
        (is (= RuntimeException (type add-result)))
        (is (= RuntimeException (type add-result-2)))

        (is (not (empty? (:channel-list add-result-3))))
        (is (vector? (:channel-list add-result-3)))

        (is (not (empty? (:channel-list @kernel/*SYSTEM*))))
        (is (map? (first (:channel-list @kernel/*SYSTEM*))))))

  (testing "on kernel bootstrap, SHOULD have kernel channel"

      (let [xx (kernel/start-system)
            result (kernel/get-kernel-channel)]

        (is (not (nil? result)))
        (is (= "kernel-channel" (:id result)))))


  #_(testing "on kernel bootstrap, SHOULD have 1 kernel-recieve function"

      (let [xx (kernel/start-system) ]

        (should-not (empty? (:recieve-fns @kernel/*SYSTEM*)))
        (should (fn? (-> @kernel/*SYSTEM* :recieve-fns first :fn)))

        ;; sending on the kernel channel should spark the kernel retrieve
        ;; ...
        ))

  ;; Commuincating with Plugins
  (testing "We can get the Plugin's plugin function"
    (let [plugin-fn (kernel/get-plugin-fn 'heartbeat.plugin)]
      (is (fn? @plugin-fn))))

  (testing "We can invoke the Plugin's plugin function"
    (let [plugin-receive (kernel/invoke-plugin-fn 'heartbeat.plugin)]
      (is (fn? plugin-receive))))

  #_(testing "We can attach the plugin to our System"
    (let [plugin-result (kernel/attach-plugin 'heartbeat.plugin)]

      (println "... " plugin-result)
      #_(is (map? plugin-result))))


  #_(testing "Handshake 1: Invoke plugin's (plugin) function"

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
