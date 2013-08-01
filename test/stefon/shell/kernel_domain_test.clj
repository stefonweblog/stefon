 (ns stefon.shell.kernel-domain-test
  (:require [midje.sweet :refer :all]
            [lamina.core :as lamina]

            [stefon.shell :as shell]
            [stefon.shell.plugin :as plugin]
            [stefon.shell.kernel :as kernel]))



(against-background [(before :facts (shell/start-system))
                     (after :facts (shell/stop-system))]


                    ;; ==== Shortened Tests for Asset & Tag functional decorators
                    (fact "Create an Asset"

                          (let [r1 (kernel/create-asset "binary-goo" "image")]

                            r1 =not=> nil?
                            (type r1) => stefon.domain.Asset
                            (count (:assets @(kernel/get-system))) => 1))


                    (fact "Retrieve an Asset"

                          (let [r1 (kernel/create-asset "binary-goo" "video")

                                assetID (:id r1)
                                r2 (kernel/retrieve-asset assetID)]

                            r2 =not=> nil?
                            (type r2) => stefon.domain.Asset
                            (:type r2) => "video"))


                    (fact "Create a Tag"

                          (let [r1 (kernel/create-tag "clojure")]

                            r1 =not=> nil?
                            (type r1) => stefon.domain.Tag
                            (count (:tags @(kernel/get-system))) => 1))


                    (fact "Retrieve a Tag"

                          (let [r1 (kernel/create-tag "ruby")

                                tagID (:id r1)
                                r2 (kernel/retrieve-tag tagID)]

                            r2 =not=> nil?
                            (type r2) => stefon.domain.Tag
                            (:name r2) => "ruby")))
