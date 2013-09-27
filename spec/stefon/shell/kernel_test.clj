(ns stefon.shell.kernel-test

  (:require [speclj.core :refer :all]
            [clojure.core.async :as async :refer :all]

            [stefon.shell.kernel :as kernel]))


(describe "Basic kernel functions"

          (it "Should already have a core channel-list "

              (println ">> Huh ?? > " @kernel/*SYSTEM*)

              (should-not-be-nil (:channel-list @kernel/*SYSTEM*))
              (should (vector? (:channel-list @kernel/*SYSTEM*))))

          (it "Should be able to add channels to a list"

              ;;
              (let [new-channel (chan)
                    add-result (try (kernel/add-to-channel-list new-channel) (catch Exception e e))
                    add-result-2 (try (kernel/add-to-channel-list {:id 2 :channel new-channel}) (catch Exception e e))
                    add-result-3 (try (kernel/add-to-channel-list {:id "ID" :channel new-channel}))]

                (should-not-be-nil add-result)
                (should (= RuntimeException (type add-result)))
                (should (= RuntimeException (type add-result-2)))

                (should-not (empty? add-result-3))
                (should (vector? add-result-3))

                (should-not (empty? (:channel-list @kernel/*SYSTEM*)))
                (should (map? (first (:channel-list @kernel/*SYSTEM*))))))

          (it "on kernel bootstrap, SHOULD have kernel channel"

              (let [xx (kernel/start-system)
                    result (kernel/get-kernel-channel)]

                (should-not-be-nil result)
                (should= "kernel-channel" (:id result))))

          #_(it "on kernel bootstrap, SHOULD have 1 kernel-recieve function")

          #_(it "on attaching a plugin, plugin SHOULD have 1 new send fn on kernel-channel")
          #_(it "on attaching a plugin, plugin SHOULD have 1 new recieve fn on the new-channel")
          #_(it "on attaching a plugin, kernel SHOULD have 1 new send fn on the new-channel")

          ;; PLUGIN
          #_(it "Should send a message that the kernel DOES understand, then forwards (check for recursive message)")
          #_(it "Should send a message that the kernel DOES NOT understand, just forwards (check for recursive message)")

          #_(it "Should send a message from plugin to kernel, and get a return value")
          #_(it "Should send a message from kernel to plugin(s), and each plugin should give a response to JUST kernel")
          #_(it "Should send a message from plugin1 -> kernel -> plugin2; then cascade return value from plugin2 -> kernel -> plugin1")
          #_(it "Should test CASCADE results with datomic plugin"))
