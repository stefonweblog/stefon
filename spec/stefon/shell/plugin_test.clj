(ns stefon.shell.plugin-test

  (:require [speclj.core :refer :all]
            [clojure.core.async :as async :refer :all]
            [stefon.shell.plugin :as plugin]))


(describe "Basic plugin functions"

          (it "Should be able to create a channel (incl. kernel channel)"

              (let [r1 (plugin/generate-channel)
                    r2 (try (plugin/generate-channel :mykey) (catch Exception e e))]

                (should-not-be-nil r1)
                (should= '(:id :channel) (keys r1))
                (should (string? (:id r1)))

                (should-not-be-nil r2)
                (should= RuntimeException (type r2)))

              (let [r3 (plugin/generate-kernel-channel)]

                (should-not-be-nil r3)
                (should= '(:id :channel) (keys r3))
                (should= "kernel-channel" (:id r3))))

          (it "Should be able to get a channel (incl. kernel channel)"

              (let [channel-list [(plugin/generate-kernel-channel)]

                    r1 (plugin/generate-kernel-channel)]

                (should-not-be-nil r1)
                (should= "kernel-channel" (:id r1))))

          (it "Should be able to generate a send function"

              (let [new-channel (chan)

                    sendfn (plugin/generate-send-fn new-channel)
                    xx (sendfn {:fu :bar})

                    rvalue (<!! new-channel)]

                (should (fn? sendfn))
                (should= {:fu :bar} rvalue)))

          (it "Should be able to generate a recieve function"

              (let [new-channel (chan)

                    recievefn (plugin/generate-recieve-fn new-channel)

                    result (promise)
                    xx (recievefn (fn [msg] (deliver result msg)))
                    xx (>!! new-channel {:fu :bar})]

                (should-not-be-nil @result)
                (should= {:fu :bar} @result))))
