(ns stefon.shell.kernel-test

  (:require [speclj.core :refer :all]
            [clojure.core.async :as async :refer :all]

            [stefon.shell.kernel :as kernel]))


(describe "Basic kernel functions"

          (it "Should already have a core channel-list "

              (let [xx (kernel/start-system)]

                (should-not-be-nil (:channel-list @kernel/*SYSTEM*))
                (should (vector? (:channel-list @kernel/*SYSTEM*)))))


          (it "Should be able to add channels to a list"

              ;;
              (let [new-channel (chan)

                    xx (kernel/start-system)

                    add-result (try (kernel/add-to-channel-list new-channel) (catch Exception e e))
                    add-result-2 (try (kernel/add-to-channel-list {:id 2 :channel new-channel}) (catch Exception e e))
                    add-result-3 (try (kernel/add-to-channel-list {:id "ID" :channel new-channel}) (catch Exception e e))]

                (should-not-be-nil add-result)
                (should (= RuntimeException (type add-result)))
                (should (= RuntimeException (type add-result-2)))

                (should-not (empty? (:channel-list add-result-3)))
                (should (vector? (:channel-list add-result-3)))

                (should-not (empty? (:channel-list @kernel/*SYSTEM*)))
                (should (map? (first (:channel-list @kernel/*SYSTEM*))))))

          (it "on kernel bootstrap, SHOULD have kernel channel"

              (let [xx (kernel/start-system)
                    result (kernel/get-kernel-channel)]

                (should-not-be-nil result)
                (should= "kernel-channel" (:id result))))


          (it "on kernel bootstrap, SHOULD have 1 kernel-recieve function"

              (let [xx (kernel/start-system) ]

                (should-not (empty? (:recieve-fns @kernel/*SYSTEM*)))
                (should (fn? (-> @kernel/*SYSTEM* :recieve-fns first :fn)))

                ;; sending on the kernel channel should spark the kernel retrieve
                ;; ...
                ))

          (it "on attaching a plugin, plugin SHOULD have 1 new send fn on kernel-channel"

              (let [xx (kernel/start-system)

                    handlerfn (fn [msg] )
                    result (kernel/attach-plugin handlerfn)]


                ((:sendfn result) {:id "asdf" :message {:fu :bar}})
                ((first (:send-fns @kernel/*SYSTEM*)) {:id "kernel-id" :message {:from :kernel}})

                (should (fn? (:sendfn result)))

                ;; using the send fn should spark the kernel retrieve
                ;; ...
                ))

          (it "on attaching a plugin, plugin SHOULD have 1 new recieve fn on the new-channel"

              (let [xx (kernel/start-system)

                    handlerfn (fn [msg] )
                    result (kernel/attach-plugin handlerfn)]

                (should (fn? (:recievefn result)))

                ;; sending on new channel, should spark plugin's rettrieve
                ;; ...
                ))


          (it "on attaching a plugin, kernel SHOULD have 1 new send fn on the new-channel"

              (let [xx (kernel/start-system)
                    xx (should (empty? (:send-fns @kernel/*SYSTEM*)))

                    handlerfn (fn [msg] )
                    result (kernel/attach-plugin handlerfn)]

                (should-not (empty? (:send-fns @kernel/*SYSTEM*)))
                (should (fn? (:fn (first (:send-fns @kernel/*SYSTEM*)))))

                ;; using new send fn, should spark plugin's retrieve
                ;; ...
                ))


          ;; PLUGIN
          (it "Should be able to send-message-raw to attached functions"

              (let [xx (kernel/start-system)

                    p1 (promise)
                    p2 (promise)
                    p3 (promise)

                    h1 (fn [msg] (deliver p1 msg))
                    h2 (fn [msg] (deliver p2 msg))
                    h3 (fn [msg] (deliver p3 msg))

                    r1 (kernel/attach-plugin h1)
                    r2 (kernel/attach-plugin h2)
                    r3 (kernel/attach-plugin h3)]

                (kernel/send-message-raw [(:id r2) (:id r3)]
                                         {:id "qwerty-1234" :fu :bar})

                (should-not (realized? p1))
                (should-not-be-nil @p2)
                (should-not-be-nil @p3)

                (should= {:id "qwerty-1234" :fu :bar} @p2)
                (should= {:id "qwerty-1234" :fu :bar} @p3)))


          (it "Should be able to send-message to attached functions :include"

              (let [xx (kernel/start-system)

                    p1 (promise)
                    p2 (promise)
                    p3 (promise)

                    h1 (fn [msg] (deliver p1 msg))
                    h2 (fn [msg] (deliver p2 msg))
                    h3 (fn [msg] (deliver p3 msg))

                    r1 (kernel/attach-plugin h1)
                    r2 (kernel/attach-plugin h2)
                    r3 (kernel/attach-plugin h3)]

                (kernel/send-message {:include [(:id r2) (:id r3)]}
                                     {:id "qwerty-1234" :fu :bar})

                (should-not (realized? p1))
                (should-not-be-nil @p2)
                (should-not-be-nil @p3)

                (should= {:id "qwerty-1234" :fu :bar} @p2)
                (should= {:id "qwerty-1234" :fu :bar} @p3)))


          (it "Should be able to send-message to attached functions :exclude"

              (let [xx (kernel/start-system)

                    p1 (promise)
                    p2 (promise)
                    p3 (promise)

                    h1 (fn [msg] (deliver p1 msg))
                    h2 (fn [msg] (deliver p2 msg))
                    h3 (fn [msg] (deliver p3 msg))

                    r1 (kernel/attach-plugin h1)
                    r2 (kernel/attach-plugin h2)
                    r3 (kernel/attach-plugin h3)]

                (kernel/send-message {:exclude [(:id r2) (:id r3)]}
                                     {:id "qwerty-1234" :fu :bar})

                (should (realized? p1))
                (should-not (realized? p2))
                (should-not (realized? p3))))


          ;; include TEE infrastructure
          ;; ...

          (it "Should send a message that the kernel DOES understand, then forwards (check for recursive message)"

              (let [xx (kernel/start-system)

                    p1 (promise)
                    handlerfn (fn [msg]
                                (println ">> plugin handler CALLED > " msg)
                                (deliver p1 msg))
                    result (kernel/attach-plugin handlerfn)]

                ((:sendfn result) {:id (:id result) :message {:stefon.post.create {:parameters {:title "Latest In Biotech"
                                                                                                :content "Lorem ipsum."
                                                                                                :content-type "txt"
                                                                                                :created-date "0000"
                                                                                                :modified-date "0000"
                                                                                                :assets []
                                                                                                :tags []}} }})

                ;; check for recursive message
                ;; ...

                (should (realized? p1))
                (should-not-be-nil (:result @p1))
                (should (= stefon.domain.Post (type (:result @p1))))))


          #_(it "Should send a message that the kernel DOES NOT understand, just forwards (check for recursive message)")
          #_(it "Should send a message from plugin to kernel, and get a return value")


          #_(it "Should send a message from kernel to plugin(s), and each plugin should give a response to JUST kernel")


          #_(it "Should send a message from plugin1 -> kernel -> plugin2; then cascade return value from plugin2 -> kernel -> plugin1")


          #_(it "Should test CASCADE results with datomic plugin"))
