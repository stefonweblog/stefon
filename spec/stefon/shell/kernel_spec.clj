(ns stefon.shell.kernel-spec

  (:require [speclj.core :refer :all]
            [clojure.core.async :as async]

            [stefon.shell :as shell]
            [stefon.shell.kernel :as kernel]))


(describe "Setup"

          (it "Setup a basic kernel channel"

              (let [system-atom (atom (shell/create-system))
                    kchannel (kernel/init-kernel-channel system-atom)]

                ;; ensure an async channel is returned
                (should-not-be-nil kchannel)

                ;; should still be the system
                (should= clojure.lang.Atom (type system-atom))
                (should= clojure.lang.PersistentArrayMap (type @system-atom))

                ;; should have :kernel-channel key with a value of an async channel
                (should-contain :kernel-channel @kchannel)
                ))


          (it "Get kernel channel"

              (let [system-atom (atom (shell/create-system))
                    kchannel (kernel/get-kernel-channel system-atom)]

                (should-not-be-nil kchannel))))

#_(describe "Core kernel functions"

          (before (def kchannel (kernel/init-channel)))

          (it "Test basic message passing in kernel channel"

              #_(let [one-msg (async/go (async/<! kchannel))
                    send-result (async/go (async/>! {:fu :bar}))]


                ;; ensure kernel can recieve 1 message

                ;; ensure kernel can receive multiple messages

                ;; ensure we can't double create a channel

                #_(should= 1 1))

              )

          )
