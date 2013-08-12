 (ns stefon.shell.kernel-domain-test

   (:require [speclj.core :refer :all]
             [lamina.core :as lamina]

             [stefon.shell :as shell]
             [stefon.shell.plugin :as plugin]
             [stefon.shell.kernel :as kernel]))


(describe "one"

          (before (shell/start-system))
          (after (shell/stop-system))


          ;; ==== Shortened Tests for Asset & Tag functional decorators
          (it "Create an Asset"

                (let [r1 (kernel/create-asset "binary-goo" "image")]

                  (should-not-be-nil r1)
                  (should= stefon.domain.Asset (type r1))
                  (should= 1 (count (:assets @(kernel/get-system))))))


          (it "Retrieve an Asset"

                (let [r1 (kernel/create-asset "binary-goo" "video")

                      assetID (:id r1)
                      r2 (kernel/retrieve-asset assetID)]

                  (should-not-be-nil r2)

                  (should= stefon.domain.Asset (type r2))
                  (should= "video" (:type r2))))


          (it "Create a Tag"

                (let [r1 (kernel/create-tag "clojure")]

                  (should-not-be-nil r1)

                  (should= stefon.domain.Tag (type r1))
                  (should= 1 (count (:tags @(kernel/get-system))))))


          (it "Retrieve a Tag"

                (let [r1 (kernel/create-tag "ruby")

                      tagID (:id r1)
                      r2 (kernel/retrieve-tag tagID)]

                  (should-not-be-nil r2)

                  (should= stefon.domain.Tag (type r2))
                  (should= "ruby" (:name r2)))))
