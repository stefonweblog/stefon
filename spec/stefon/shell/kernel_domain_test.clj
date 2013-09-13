 (ns stefon.shell.kernel-domain-test

   (:require [speclj.core :refer :all]
             [lamina.core :as lamina]

             [stefon.shell :as shell]
             [stefon.shell.plugin :as plugin]
             [stefon.shell.kernel :as kernel]
             [stefon.domain :as domain]))

(describe "one"

          (before (shell/start-system))
          (after (shell/stop-system))


          ;; ==== Shortened Tests for Asset & Tag functional decorators
          (it "Create an Asset"

                (let [r1 (kernel/create-asset "myimage" "image" "binary-goo")]

                  (should-not-be-nil r1)
                  (should= stefon.domain.Asset (type r1))
                  (should= 1 (count (kernel/get-assets)))))


          (it "Retrieve an Asset"

                (let [r1 (kernel/create-asset "myimage" "video"  "binary-goo")

                      assetID (:id r1)
                      r2 (kernel/retrieve-asset assetID)]

                  (should-not-be-nil r2)

                  (should= stefon.domain.Asset (type r2))
                  (should= "video" (:type r2))))


          (it "Create a Tag"

                (let [r1 (kernel/create-tag "clojure")]

                  (should-not-be-nil r1)

                  (should= stefon.domain.Tag (type r1))
                  (should= 1 (count (kernel/get-tags)))))


          (it "Retrieve a Tag"

                (let [r1 (kernel/create-tag "ruby")

                      tagID (:id r1)
                      r2 (kernel/retrieve-tag tagID)]

                  (should-not-be-nil r2)

                  (should= stefon.domain.Tag (type r2))
                  (should= "ruby" (:name r2)))))
