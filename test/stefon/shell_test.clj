(ns stefon.shell-test

  (:use [midje.sweet])
  (:require [stefon.shell :as shell]
            [clojure.pprint :as pprint]))



(against-background [(before :facts (shell/start-system))
                     (after :facts (shell/stop-system))]


                    ;; ====
                    (fact "Create the System"

                          (let [system (shell/create-system)]

                            ;; checking i) not nil, ii) keys and iii) content
                            system =not=> nil?
                            (keys system) => (contains #{:posts :assets :tags})
                            system => (just [[:posts []] [:assets []] [:tags []] [:channel-spout nil] [:channel-sink nil]])))


                    (fact "Start the System"

                          (let [result (shell/start-system)]

                            (ns-name *ns*) => 'stefon.shell))


                    (fact "Stop the System"

                          (let [started (shell/start-system)
                                stopped (shell/stop-system)]

                            (ns-name *ns*) => 'user))



                    ;; ==== Shortened Tests for Asset & Tag functional decorators
                    (fact "Create an Asset"

                          (let [r1 (shell/create-asset "binary-goo" "image")]

                            r1 =not=> nil?
                            (type r1) => stefon.domain.Asset
                            (count (:assets @shell/*SYSTEM*)) => 1))


                    (fact "Retrieve an Asset"

                          (let [r1 (shell/create-asset "binary-goo" "video")

                                assetID (:id r1)
                                r2 (shell/retrieve-asset assetID)]

                            r2 =not=> nil?
                            (type r2) => stefon.domain.Asset
                            (:type r2) => "video"))


                    (fact "Create a Tag"

                          (let [r1 (shell/create-tag "clojure")]

                            r1 =not=> nil?
                            (type r1) => stefon.domain.Tag
                            (count (:tags @shell/*SYSTEM*)) => 1))


                    (fact "Retrieve a Tag"

                          (let [r1 (shell/create-tag "ruby")

                                tagID (:id r1)
                                r2 (shell/retrieve-tag tagID)]

                            r2 =not=> nil?
                            (type r2) => stefon.domain.Tag
                            (:name r2) => "ruby")))
