(ns stefon.shell.kernel-spec

  (:require [speclj.core :refer :all]
            [clojure.core.async :as async]

            [stefon.shell.kernel :as kernel]))


(describe "Setup"

          (it "Setup a basic kernel channel"

              (let [kchannel (kernel/init-channel)]

                ;; ensure an async channel is returned
                (should-not-be-nil kchannel))))

(describe "Core kernel functions"

          (before (def kchannel (kernel/init-channel)))

          (it "Test basic message passing in kernel channel"

              (let [one-msg (async/go (async/<! kchannel))]

                ;; ensure an async channel is returned
                (should-not-be-nil kchannel)

                ;; ensure kernel can recieve 1 message

                ;; ensure kernel can receive multiple messages

                ;; ensure we can't double create a channel

                #_(should= 1 1))

              )

          )
