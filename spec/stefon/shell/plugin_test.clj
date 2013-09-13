(ns stefon.shell.plugin-test

  (:require [speclj.core :refer :all]
            [lamina.core :as lamina]

            [stefon.shell :as shell]
            [stefon.shell.kernel :as kernel]
            [stefon.shell.plugin :as plugin]
            [stefon.domain :as domain]))

(describe "one"

          (before (shell/start-system))
          (after (shell/stop-system))


          ;; ====
          (it "Testing that channels exist in the returned system"

              (let [result-system (plugin/create-plugin-system @(kernel/get-system))]


                (should-not-be-nil (some #{:channel-spout :channel-sink} (keys result-system)))

                (should-not-be-nil (:channel-spout result-system))
                (should-not-be-nil (:channel-sink result-system))))


          (it "Test that shell/*SYSTEM* has the attached channel(s)"

              (should-not-be-nil (:channel-spout @(kernel/get-system)))
              (should-not-be-nil (:channel-sink @(kernel/get-system))))


          ;; ====
          (it "Ensure we're getting back a sender function"

             (let [handler-fn (fn [event])
                   sender-fn (plugin/attach-plugin @(kernel/get-system) handler-fn)]

               (should-not-be-nil sender-fn)
               (should (fn? sender-fn))))



          (it "Ensure that result begins as empty, and then occupied after a message is sent"

              (let [result-event (atom nil)
                    handler-fn (fn [event] (swap! result-event (fn [i] event)))
                    sender-fn (plugin/attach-plugin @(kernel/get-system) handler-fn)]

                ;; result-event should initially be empty
                (should-be-nil @result-event)

                ;; publish a message
                (plugin/publish-event @(kernel/get-system) {:fu :bar})

                ;; now should have content
                (should-not-be-nil @result-event)
                (should-contain :fu (keys @result-event))))


          ;; TODO - howto test that a function was invoked multiple times
          #_(it "Test for multiple sends from kernel"

                (let [handler-1 (fn [inp] inp)
                      sender-1 (plugin/attach-plugin @(kernel/get-system) handler-1)
                      call-multiple (fn []

                                      (plugin/publish-event @(kernel/get-system) {:fu :bar}) => nil?
                                      (plugin/publish-event @(kernel/get-system) {:interrupt :software}) => nil?
                                      nil)]

                  (call-multiple) => nil?
                  (provided
                   (handler-1 {}) => {} :times 2)))

          (it "Test for multiple handlers receiving a send"

                (let [r1 (atom nil)
                      r2 (atom nil)

                      h1 (fn [inp] (swap! r1 (fn [i] inp)))
                      h2 (fn [inp] (swap! r2 (fn [i] inp)))

                      sender-1 (plugin/attach-plugin @(kernel/get-system) h1)
                      sender-2 (plugin/attach-plugin @(kernel/get-system) h2)]

                  (plugin/publish-event @(kernel/get-system) {:fu :bar})

                  (should-not-be-nil @r1)
                  (should-not-be-nil @r2)

                  (should-contain :fu (keys @r1))
                  (should-contain :fu (keys @r2)))))
