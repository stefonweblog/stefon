(ns stefon.shell.plugin-test
  (:require [midje.sweet :refer :all]
            [lamina.core :as lamina]

            [stefon.shell :as shell]
            [stefon.shell.plugin :as plugin]))


(against-background [(before :facts (shell/start-system))
                     (after :facts (shell/stop-system))]


                    ;; ====
                    (let [result (plugin/create-plugin-system @shell/*SYSTEM*)]

                      (fact "" 1 => 1))


                    #_(let [[client server] (lamina/channel-pair)]

                      (fact ""
                            1 => 1))


)
