(ns stefon.shell.plugin-test
  (:require [midje.sweet :refer :all]
            [lamina.core :as lamina]

            [stefon.shell :as shell]
            [stefon.shell.plugin :as plugin]))


(against-background [(before :contents (shell/start-system))
                     (after :contents  (shell/stop-system))]

                    ;; ====
                    (let [result-system (plugin/create-plugin-system @shell/*SYSTEM*)]

                      (fact "Testing that channels exist in the returned system"

                            (keys result-system) => (contains #{:channel-spout :channel-sink})
                            (:channel-spout result-system) =not=> nil?
                            (:channel-sink result-system) =not=> nil?))

                    (fact "Test that shell/*SYSTEM* has the attached channel(s)"

                          (:channel-spout @shell/*SYSTEM*) =not=> nil?
                          (:channel-sink @shell/*SYSTEM*) =not=> nil?)


                    ;; ====
                    (let [handler-fn (fn [event])
                          sender-fn (plugin/attach-plugin shell/*SYSTEM* handler-fn)]

                      (fact "Ensure we're getting back a sender function"

                            sender-fn =not=> nil?
                            sender-fn => fn?))

                    (let [result-event (atom nil)
                          handler-fn (fn [event] (swap! result-event (fn [i] event)))
                          sender-fn (plugin/attach-plugin shell/*SYSTEM* handler-fn)]

                      (fact "Ensure that result begins as empty" @result-event => nil?)
                      (fact "Ensure that kernel sending messages is recieved by handler function"

                            (plugin/publish-event shell/*SYSTEM* {:fu :bar})

                            @result-event =not=> nil?
                            (keys @result-event)  => (contains #{:fu}))))


(against-background [(before :facts (shell/start-system))
                     (after :facts (shell/stop-system))]

                    (fact "Test for multiple sends from kernel"

                            (let [handler-1 (fn [inp] inp)
                                  sender-1 (plugin/attach-plugin shell/*SYSTEM* handler-1)
                                  call-multiple (fn []

                                                  (plugin/publish-event shell/*SYSTEM* {:fu :bar}) => nil?
                                                  (plugin/publish-event shell/*SYSTEM* {:interrupt :software}) => nil?
                                                  nil)]

                              (call-multiple) => nil?
                              (provided
                               (handler-1 {}) => {} :times 2)))

                    (fact "Test for multiple handlers receiving a send"

                          (let [r1 (atom nil)
                                r2 (atom nil)

                                h1 (fn [inp] (swap! r1 (fn [i] inp)))
                                h2 (fn [inp] (swap! r2 (fn [i] inp)))

                                sender-1 (plugin/attach-plugin shell/*SYSTEM* h1)
                                sender-2 (plugin/attach-plugin shell/*SYSTEM* h2)]

                            (plugin/publish-event shell/*SYSTEM* {:fu :bar})

                            @r1 =not=> nil?
                            @r2 =not=> nil?

                            (keys @r1) => (contains #{:fu})
                            (keys @r2) => (contains #{:fu}))))

(against-background [(before :facts (shell/start-system))
                     (after :facts (shell/stop-system))]

                    (fact 1 => 1))
