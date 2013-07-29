(ns stefon.plugin-test
  (:use [midje.sweet]))


(against-background [(before :facts 1)
                     (after :facts 2)]


                    ;; ====
                    (fact ""
                          1 => 1)
                    )
