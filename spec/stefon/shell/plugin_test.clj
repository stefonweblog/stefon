(ns stefon.shell.plugin-test

  (:require [speclj.core :refer :all]
            [lamina.core :as lamina]

            [stefon.shell :as shell]
            [stefon.shell.kernel :as kernel]
            [stefon.shell.plugin :as plugin]))


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
          #_(let [handler-fn (fn [event])
                sender-fn (plugin/attach-plugin @(kernel/get-system) handler-fn)]

            #_(fact "Ensure we're getting back a sender function"

                  sender-fn =not=> nil?
                  sender-fn => fn?))

          #_(let [result-event (atom nil)
                handler-fn (fn [event] (swap! result-event (fn [i] event)))
                sender-fn (plugin/attach-plugin @(kernel/get-system) handler-fn)]

            #_(fact "Ensure that result begins as empty" @result-event => nil?)
            #_(fact "Ensure that kernel sending messages is recieved by handler function"

                  (plugin/publish-event @(kernel/get-system) {:fu :bar})

                  @result-event =not=> nil?
                  (keys @result-event)  => (contains #{:fu}))))


#_(against-background [(before :facts (shell/start-system))
                     (after :facts (shell/stop-system))]

                    (fact "Test for multiple sends from kernel"

                            (let [handler-1 (fn [inp] inp)
                                  sender-1 (plugin/attach-plugin @(kernel/get-system) handler-1)
                                  call-multiple (fn []

                                                  (plugin/publish-event @(kernel/get-system) {:fu :bar}) => nil?
                                                  (plugin/publish-event @(kernel/get-system) {:interrupt :software}) => nil?
                                                  nil)]

                              (call-multiple) => nil?
                              (provided
                               (handler-1 {}) => {} :times 2)))

                    (fact "Test for multiple handlers receiving a send"

                          (let [r1 (atom nil)
                                r2 (atom nil)

                                h1 (fn [inp] (swap! r1 (fn [i] inp)))
                                h2 (fn [inp] (swap! r2 (fn [i] inp)))

                                sender-1 (plugin/attach-plugin @(kernel/get-system) h1)
                                sender-2 (plugin/attach-plugin @(kernel/get-system) h2)]

                            (plugin/publish-event (kernel/get-system) {:fu :bar})

                            @r1 =not=> nil?
                            @r2 =not=> nil?

                            (keys @r1) => (contains #{:fu})
                            (keys @r2) => (contains #{:fu}))))
