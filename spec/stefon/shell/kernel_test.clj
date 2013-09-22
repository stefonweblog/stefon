(ns stefon.shell.kernel-test

  (:require [speclj.core :refer :all]
            [stefon.shell.kernel :as kernel]))


(describe "Basic kernel functions"

          (it "Should already have a core channel-list "

              (should-not-be-nil kernel/channel-list)
              (should (vector? @kernel/channel-list)))

          (it "Should be able to all channels to a list"

              ))
