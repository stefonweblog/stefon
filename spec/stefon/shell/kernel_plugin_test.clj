(ns stefon.shell.kernel-plugin-test

  (:require [speclj.core :refer :all]
            [lamina.core :as lamina]

            [stefon.shell :as shell]
            [stefon.shell.plugin :as plugin]
            [stefon.shell.kernel :as kernel]
            [stefon.domain :as domain]))


(describe "one"

          (it "Test kernel receive from plugin message"

              (let [system (plugin/create-plugin-system (shell/create-system))
                    handler (fn [inp] inp)
                    sender (plugin/attach-plugin system handler)

                    result-event (atom nil)
                    kernel-handler (fn [inp] (swap! result-event (fn [i] (:send-event inp))))

                    xxx (kernel/attach-kernel system kernel-handler)
                    xxy (sender {:fu :bar :qwerty :board})]

                (should-not-be-nil @result-event)

                (should= clojure.lang.PersistentArrayMap (type @result-event))
                (should-contain :fu (keys @result-event))
                (should-contain :qwerty (keys @result-event))))


          (it "Test attaching kernel to the shell"

                (let [system (shell/create-system)
                      result-event (atom nil)
                      kernel-handler (fn [inp] (swap! result-event (fn [i] (:send-event inp))))

                      system-with-handler (shell/start-system system kernel-handler)

                      ;; something needs to send an event
                      handler (fn [inp] inp)
                      sender (plugin/attach-plugin @system-with-handler handler)

                      result-send (sender {:fu :bar :qwerty :board})]

                  (should-not-be-nil @result-event)
                  (should= clojure.lang.PersistentArrayMap (type @result-event))
                  (should-contain :fu (keys @result-event))
                  (should-contain :qwerty (keys @result-event))))


          (it "Test kernel action mapping:"

              (let [system (shell/create-system)
                    system-with-handler (shell/start-system system)

                    ;; something needs to send an event
                    handler (fn [inp]

                              (println (str "test plugin handler [" inp "]")))
                    sender (plugin/attach-plugin @system-with-handler handler)

                    result-send (sender {:stefon.post.create {:parameters {:title "Latest In Biotech" :content "Lorem ipsum." :created-date "0000"}}
                                         :fu :bar})]

                (should true))

              ;; TODO - so far just testing with println ; find a better way to test kernel handler

              ;; did handler receive event

              ;; did correct function get triggered, from a mapped action
              ;; did the triggered function, send out a message to other plugins after the fact

              ;; if applicable, did multiple functions get triggred

              ;; did the kernel ignore actions it did not recognize
              ;; did the kernel forward on, to other plugins, the actions it did not recognize


              ;; howto test how many times a function was called
              )

          (it "Test kernel action mapping: :stefon.domain ... Also tests that the plugin gets a promise-result from to their evaluation"

              (let [system (shell/create-system)
                    system-with-handler (shell/start-system system)

                    ;; something needs to send an event
                    handler (fn [inp]

                              (println (str "test plugin handler [" inp "]")))

                    sender (plugin/attach-plugin @system-with-handler handler)

                    result-promise (sender {:stefon.domain {:parameters nil}})]

                (should-not-be-nil @result-promise)
                (should= {:posts [], :assets [], :tags []} @result-promise)))

          )
