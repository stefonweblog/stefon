(ns stefon.shell.kernel-test
  (:use clojure.test
        midje.sweet)
  (:require [clojure.core.async :as async :refer :all]
            [stefon.core :as core]
            [stefon.shell.kernel :as kernel]
            [heartbeat.plugin :as heartbeat]))


(defn- system-shape []
  {:domain {:posts [], :assets [], :tags []},
   :channel-list [],
   :send-fns [],
   :recieve-fns [],
   :tee-fns []})

(defn- matches-domain-shape [input-shape]
  (= input-shape (system-shape)))

(defn- matches-system-shape [input-shape]
  (= input-shape {:stefon/system (system-shape)}))

(deftest test-kernel-1

  ;; Create & Manage the System
  (testing "System generation"
    (let [kernel-result (kernel/generate-system)]
      (is (not (nil? kernel-result)))
      (is (map? kernel-result))
      (is (matches-domain-shape kernel-result))))

  (testing "Kernel Startup"
    (let [start-result (kernel/start-system)]

      (is (not (nil? @start-result)))
      (is (map? @start-result))
      (is (= (keys (system-shape)) (keys (:stefon/system @start-result))) )

      (let [system-state (kernel/get-system)]

        (is (not (nil? @system-state)))
        (is (map? @system-state))
        (is (= (keys (system-shape)) (keys (:stefon/system @system-state)))))))

  (testing "Should already have a core channel-list "

      (let [xx (kernel/start-system)]

        (is (not (nil? (:channel-list @kernel/*SYSTEM*))))
        (is (vector? (:channel-list @kernel/*SYSTEM*)))))


  (testing "Should be able to add channels to a list"

      (let [new-channel (chan)

            xx (kernel/start-system)

            add-result (try
                         (kernel/add-to-channel-list new-channel)
                         (catch Exception e e))
            add-result-2 (try
                           (kernel/add-to-channel-list {:id 2 :channel new-channel})
                           (catch Exception e e))
            add-result-3 (try
                           (kernel/add-to-channel-list {:id "ID" :channel new-channel})
                           (catch Exception e e))]

        (is (not (nil? add-result)))
        (is (= RuntimeException (type add-result)))
        (is (= RuntimeException (type add-result-2)))

        (is (not (empty? (:channel-list add-result-3))))
        (is (vector? (:channel-list add-result-3)))

        (is (not (empty? (:channel-list @kernel/*SYSTEM*))))
        (is (map? (first (:channel-list @kernel/*SYSTEM*))))))

  (testing "on kernel bootstrap, SHOULD have kernel channel"

      (let [xx (kernel/start-system)
            result (kernel/get-kernel-channel)]

        (is (not (nil? result)))
        (is (= "kernel-channel" (:id result)))))


  (testing "on kernel bootstrap, SHOULD have 1 kernel-recieve function"

      (let [xx (kernel/start-system) ]

        (is (not (empty? (:recieve-fns @kernel/*SYSTEM*))))
        (is (fn? (-> @kernel/*SYSTEM* :recieve-fns first :fn)))

        ;; sending on the kernel channel should spark the kernel retrieve
        ;; ...
        ))

  #_(testing "on attaching a plugin, plugin SHOULD have 1 new send fn on kernel-channel"

      (let [xx (kernel/start-system)

            handlerfn (fn [msg] )
            result (shell/attach-plugin handlerfn)]


        ((:sendfn result) {:id "asdf" :message {:fu :bar}})
        ((first (:send-fns @kernel/*SYSTEM*)) {:id "kernel-id" :message {:from :kernel}})

        (is (fn? (:sendfn result)))

        ;; using the send fn should spark the kernel retrieve
        ;; ...
        ))

          #_(it "on attaching a plugin, plugin SHOULD have 1 new recieve fn on the new-channel"

              (let [xx (kernel/start-system)

                    handlerfn (fn [msg] )
                    result (shell/attach-plugin handlerfn)]

                (should (fn? (:recievefn result)))

                ;; sending on new channel, should spark plugin's rettrieve
                ;; ...
                ))


          #_(it "on attaching a plugin, kernel SHOULD have 1 new send fn on the new-channel"

              (let [xx (kernel/start-system)

                    ;; not necissarily tru if we are loading plugins
                    ;;xx (should (empty? (:send-fns @kernel/*SYSTEM*)))

                    handlerfn (fn [msg] )
                    result (shell/attach-plugin handlerfn)]

                (should-not (empty? (:send-fns @kernel/*SYSTEM*)))
                (should (fn? (:fn (first (:send-fns @kernel/*SYSTEM*)))))

                ;; using new send fn, should spark plugin's retrieve
                ;; ...
                ))


          ;; PLUGIN
          #_(it "Should be able to send-message-raw to attached functions"

              (let [xx (kernel/start-system)

                    p1 (promise)
                    p2 (promise)
                    p3 (promise)

                    h1 (fn [msg] (deliver p1 msg))
                    h2 (fn [msg] (deliver p2 msg))
                    h3 (fn [msg] (deliver p3 msg))

                    r1 (shell/attach-plugin h1)
                    r2 (shell/attach-plugin h2)
                    r3 (shell/attach-plugin h3)]

                (kernel/send-message-raw [(:id r2) (:id r3)]
                                         {:id "qwerty-1234" :fu :bar})

                (should-not (realized? p1))
                (should-not-be-nil @p2)
                (should-not-be-nil @p3)

                (should= {:id "qwerty-1234" :fu :bar} @p2)
                (should= {:id "qwerty-1234" :fu :bar} @p3)))


          #_(it "Should be able to send-message to attached functions :include"

              (let [xx (kernel/stop-system)
                    xx (kernel/start-system)

                    p1 (promise)
                    p2 (promise)
                    p3 (promise)

                    h1 (fn [msg] (deliver p1 msg))
                    h2 (fn [msg] (deliver p2 msg))
                    h3 (fn [msg] (deliver p3 msg))

                    r1 (shell/attach-plugin h1)
                    r2 (shell/attach-plugin h2)
                    r3 (shell/attach-plugin h3)]

                (kernel/send-message {:include [(:id r2) (:id r3)]}
                                     {:id "qwerty-1234" :fu :bar})

                (should-not (realized? p1))
                (should-not-be-nil @p2)
                (should-not-be-nil @p3)

                (should= {:id "qwerty-1234" :fu :bar} @p2)
                (should= {:id "qwerty-1234" :fu :bar} @p3)))


          #_(it "Should be able to send-message to attached functions :exclude"

              (let [xx (kernel/stop-system)
                    xx (kernel/start-system)

                    p1 (promise)
                    p2 (promise)
                    p3 (promise)

                    h1 (fn [msg]

                         (deliver p1 msg)

                         (should (realized? p1))
                         (should-not (realized? p2))
                         (should-not (realized? p3)))
                    h2 (fn [msg] (deliver p2 msg))
                    h3 (fn [msg] (deliver p3 msg))

                    r1 (shell/attach-plugin h1)
                    r2 (shell/attach-plugin h2)
                    r3 (shell/attach-plugin h3)]

                (kernel/send-message {:exclude [(:id r2) (:id r3)]}
                                     {:id "qwerty-1234" :message {:fu :bar}})

                ))


          ;; include TEE infrastructure
          ;; ...

          #_(it "Should send a message that the kernel DOES understand, then forwards (check for recursive message)"

              (let [xx (kernel/stop-system)
                    xx (kernel/start-system)

                    ptee (promise)
                    teefn (fn [msg]

                            (deliver ptee msg)

                            (should (realized? ptee))
                            (should= '(:id :message) (keys @ptee)))
                    xx (kernel/add-receive-tee teefn)

                    handlerfn (fn [msg])
                    result (shell/attach-plugin handlerfn)

                    date-one (-> (java.text.SimpleDateFormat. "MM/DD/yyyy") (.parse "09/01/2013"))]

                ((:sendfn result) {:id (:id result) :message {:stefon.post.create {:parameters {:title "Latest In Biotech"
                                                                                                :content "Lorem ipsum."
                                                                                                :content-type "txt"
                                                                                                :created-date date-one
                                                                                                :modified-date date-one
                                                                                                :assets []
                                                                                                :tags []}} }})
                ;; check for recursive message
                ;; ...

                ))


          #_(it "Should send a message that the kernel DOES NOT understand, just forwards (check for recursive message)"

              (let [xx (kernel/stop-system)
                    xx (kernel/start-system)

                    p1 (promise)
                    p2 (promise)
                    p3 (promise)

                    h1 (fn [msg] (deliver p1 msg))
                    h2 (fn [msg] (deliver p2 msg))
                    h3 (fn [msg] (deliver p3 msg))

                    r1 (shell/attach-plugin h1)
                    r2 (shell/attach-plugin h2)
                    r3 (shell/attach-plugin h3)

                    message {:id (:id r1) :message {:fu :bar}}]

                ((:sendfn r1) message)

                @p2 ;; cheating
                @p3

                (should-not (realized? p1))
                (should (realized? p2))
                (should (realized? p3))

                ;; message should be unmodified
                (should= message @p2)))


          #_(it "Should send a message from plugin to kernel, and get a return value (check for recursive message)"

              (let [xx (kernel/stop-system)
                    xx (kernel/start-system)

                    p1 (promise)
                    handlerfn (fn [msg]
                                (deliver p1 msg))
                    result (shell/attach-plugin handlerfn)

                    date-one (-> (java.text.SimpleDateFormat. "MM/DD/yyyy") (.parse "09/01/2013"))]

                ((:sendfn result) {:id (:id result) :message {:stefon.post.create {:parameters {:title "Latest In Biotech"
                                                                                                :content "Lorem ipsum."
                                                                                                :content-type "txt"
                                                                                                :created-date date-one
                                                                                                :modified-date date-one
                                                                                                :assets []
                                                                                                :tags []}} }})

                ;; check for recursive message
                ;; ...

                @p1 ;; cheating

                (should (realized? p1))
                (should-not-be-nil (:result @p1))
                (should (= stefon.domain.Post (type (:result @p1))))))



          #_(it "Should send a message from plugin1 -> kernel -> plugin2; then cascade return value from plugin2 -> kernel -> plugin1"

              (let [xx (kernel/start-system)

                    ;; gymnastics SETUP
                    h3-send (promise)
                    r3 {}


                    ;; PROMISEs
                    p1 (promise)
                    p2 (promise)
                    p3 (promise)


                    ;; HANDLERs
                    h1 (fn [msg]

                         ;;(println ">> h1 CALLED > " msg)
                         (deliver p1 msg))

                    h2 (fn [msg] (deliver p2 msg))
                    h3 (fn [msg]


                         ;; make h3 handle 'plugin.post.create'
                         (let [ppcreate (-> msg :plugin.post.create :message)]

                           ;;(println ">> h3 > " @h3-send)
                           (@h3-send msg)))


                    ;; ATTACH results
                    r1 (shell/attach-plugin h1)
                    r2 (shell/attach-plugin h2)
                    r3 (shell/attach-plugin h3)


                    ;; h3-send SETUP
                    xx (deliver h3-send (fn [msg]

                                          #_(println ">> h3-send [" {:id (:id r3)
                                                                   :origin (-> msg :plugin.post.create :id)
                                                                   :action :noop
                                                                   :result {:fu :bar}} "]")

                                          ;;(println ">> h3-send > r3 > " r3)

                                          ((:sendfn r3) {:id (:id r3)
                                                         :origin (-> msg :plugin.post.create :id)
                                                         :action :noop
                                                         :result {:fu :bar}})))

                    date-one (-> (java.text.SimpleDateFormat. "MM/DD/yyyy") (.parse "09/01/2013"))
                    message {:id (:id r1) :message {:stefon.post.create {:parameters {:title "Latest In Biotech"
                                                                                      :content "Lorem ipsum."
                                                                                      :content-type "txt"
                                                                                      :created-date date-one
                                                                                      :modified-date date-one
                                                                                      :assets []
                                                                                      :tags []}} }}]

                ((:sendfn r1) message)


                #_(println ">> TEST Result > " @p1)

                ;; p1 will be called twice
                ;; ...

                ))

          #_(it "Should be able to get a channel, after we attach a plugin"

              (let [xx (kernel/stop-system)
                    xx (kernel/start-system)

                    p1 (promise)
                    h1 (fn [msg]
                          (deliver p1 msg))
                    r1 (shell/attach-plugin h1)
                    rid (:id r1)

                    message {:id rid :message {:stefon.domain.channel {:parameters {:ID rid}}}}]

                (def one ((:sendfn r1) message))

                (should (realized? p1))
                (should (map? (:result @p1)))
                (should (= rid (-> @p1 :result :id)))
                (should (= clojure.core.async.impl.channels.ManyToManyChannel
                           (type (-> @p1 :result :channel)))) ))
  )

(deftest test-kernel-2

  ;; Commuincating with Plugins
  (testing "We can get the Plugin's plugin function"
    (let [plugin-fn (kernel/get-plugin-fn 'heartbeat.plugin)]
      (is (fn? @plugin-fn))))

  (testing "We can invoke the Plugin's plugin function"
    (let [plugin-receive (kernel/invoke-plugin-fn 'heartbeat.plugin)]
      (is (fn? plugin-receive))))

  #_(testing "We can attach the plugin to our System"
    (let [plugin-result (kernel/attach-plugin 'heartbeat.plugin)]

      (println "... " plugin-result)
      #_(is (map? plugin-result))))


  #_(testing "Handshake 1: Invoke plugin's (plugin) function"

    (let [plugin-receive (kernel/load-plugin 'heartbeat.plugin)]

      )

    ;; Handshake 2: Return channel and send & receive functions

    ;; Plugin requests Schema - deliver

    ;; Plugin sends heartbeat - deliver

    ;; Plugin adds a post - deliver

    ;; Plugin adds a post; Second Plugin also responds - deliver

    )
  #_(testing "Handshake 2: Return channel and send & receive functions"

    (let [plugin-receive (kernel/load-plugin 'heartbeat.plugin)
          plugin-ackack (kernel/plugin-handshake-ack 'heartbeat.plugin)]

      )

    ))
