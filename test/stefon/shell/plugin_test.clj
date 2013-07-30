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
                            (keys result-system) => (contains #{:channel-spout :channel-sink})))

)
