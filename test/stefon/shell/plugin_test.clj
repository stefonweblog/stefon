(ns stefon.shell.plugin-test

  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [clojure.core.async :as async :refer :all]
            [stefon.shell.kernel :as kernel]
            [stefon.shell.plugin :as plugin]))


(deftest basic-plugin-functions

  (testing "Should be able to create a channel (incl. kernel channel)"

      (let [r1 (plugin/generate-channel)
            r2 (try (plugin/generate-channel :mykey) (catch Exception e e))]

        (is (not (nil? r1)))
        (is (= '(:id :channel) (keys r1)))
        (is (string? (:id r1)))

        (is (not (nil? r2)))
        (is (= RuntimeException (type r2))))

      (let [r3 (plugin/generate-kernel-channel)]

        (is (not (nil? r3)))
        (is (= '(:id :channel) (keys r3)))
        (is (= "kernel-channel" (:id r3)))))

  (testing "Should be able to get a channel (incl. kernel channel)"

      (let [channel-list [(plugin/generate-kernel-channel)]

            r1 (plugin/generate-kernel-channel)]

        (is (not (nil? r1)))
        (is (= "kernel-channel" (:id r1)))))

  (testing "Should be able to generate a send function"

      (let [new-channel (chan)

            sendfn (plugin/generate-send-fn new-channel)
            xx (sendfn {:id "asdf" :message {:fu :bar}})

            rvalue (<!! new-channel)]

        (is (fn? sendfn))
        (is (= {:id "asdf" :message {:fu :bar}} rvalue))))

  (testing "Should be able to generate a recieve function"

      (let [new-channel (chan)
            recievefn (plugin/generate-recieve-fn new-channel)
            system-atom (atom {:stefon/system (kernel/generate-system)})
            result (promise)
            xx (recievefn system-atom
                          (fn [system-atom msg] (deliver result msg)))
            xx (>!! new-channel {:fu :bar})]

        (is (not (nil? @result)))
        (is (= {:fu :bar} @result)))))
