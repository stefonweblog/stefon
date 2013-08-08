(ns stefon.shell.kernel-spec

  (:require [speclj.core :refer :all]))

(describe "A test"
          (it "Should fail"
              (should= 1 1)))

(run-specs)
