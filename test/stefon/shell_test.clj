(ns stefon.shell-test

  (:use [midje.sweet])
  (:require [stefon.shell :as shell]))



(against-background [(before :facts (shell/start-system))
                     (after :facts (shell/stop-system))]


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

                            (ns-name *ns*) => 'user))


                    (fact "Create a Post"

                          (let [r1 (shell/create-post "t" "c" "0000")]

                            (count (:posts @shell/*SYSTEM*)) => 1))

                    (fact "Retrieve a Post" 1 => 1)

                    (fact "Update a Post" 1 => 1)

                    (fact "Delete a Post" 1 => 1)

                    (fact "Find Posts" 1 => 1)

                    (fact "List all Posts" 1 => 1))
