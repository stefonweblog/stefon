(ns stefon.shell.kernel-test

  (:require [speclj.core :refer :all]
            [clojure.core.async :as async :refer :all]

            [stefon.shell.kernel :as kernel]))


(describe "Basic kernel functions"

          (it "Should already have a core channel-list "

              (should-not-be-nil kernel/channel-list)
              (should (vector? @kernel/channel-list)))

          (it "Should be able to all channels to a list"

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

                (should-not (empty? @kernel/channel-list))
                (should (map? (first @kernel/channel-list))))))
