(ns stefon.shell-test

  (:use [midje.sweet])
  (:require [stefon.shell :as shell]))


(against-background [(after :facts (in-ns 'user))]


                    (fact "Create the System"

                          (let [system (shell/create-system)]

                            system =not=> nil?))


                    (fact "Start the System"

                          (let [result (shell/start-system)]

                            (ns-name *ns*) => 'stefon.shell))


                    (fact "Stop the System"

                          (let [result (shell/stop-system)])))
