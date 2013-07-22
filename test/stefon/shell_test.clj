(ns stefon.shell-test

  (:use [midje.sweet])
  (:require [stefon.shell :as shell]))


(against-background [(after :facts (in-ns 'user))]


                    (fact "Create the System"

                          (let [system (shell/create-system)]

                            ;; checking i) not nil, ii) keys and iii) content
                            system =not=> nil?
                            (keys system) => (contains #{:posts :assets :tags})
                            system => (just [[:posts []] [:assets []] [:tags []]])))


                    (fact "Start the System"

                          (let [result (shell/start-system)]

                            (ns-name *ns*) => 'stefon.shell))


                    (fact "Stop the System"

                          (let [started (shell/start-system)
                                stopped (shell/stop-system)]

                            (ns-name *ns*) => 'user)))
