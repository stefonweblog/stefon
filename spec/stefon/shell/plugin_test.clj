(ns stefon.shell.plugin-test

  (:require [speclj.core :refer :all]
            [stefon.shell.plugin :as plugin]))


(describe "Basic plugin functions"

          (it "Should be able to create a channel (incl. kernel channel)"

              (let [r1 (plugin/generate-channel)
                    r2 (try (plugin/generate-channel :mykey) (catch Exception e e))]

                (should-not-be-nil r1)
                (should= '(:id :channel) (keys r1))
                (should (string? (:id r1)))

                (should-not-be-nil r2)
                (should= RuntimeException (type r2))))

          (it "Should be able to get a channel (incl. kernel channel)"

              ))
