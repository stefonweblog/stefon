(ns stefon.shell.kernel

  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.core.async :as async :refer :all]
            [schema.core :as s]

            [stefon.schema :as ss]
            [stefon.domain :as domain]
            [stefon.shell.plugin :as plugin]
            [stefon.shell.functions :as functions]))


(ss/turn-on-validation)


;; SYSTEM structure & functions
(def ^{:doc "In memory representation of the running system structures"} ^:dynamic *SYSTEM* (atom nil))

(defn generate-system []
  {:system nil
   :channel-list []

   :send-fns []
   :recieve-fns []})


(defn- add-to-generic [lookup-key thing]
  (swap! *SYSTEM* (fn [inp]
                    (update-in inp [lookup-key] (fn [ii] (into [] (conj ii thing)))))))

(s/defn add-to-channel-list [new-channel :- { (s/required-key :id) s/String
                                              (s/required-key :channel) s/Any}]
  (add-to-generic :channel-list new-channel))

(s/defn add-to-recievefns [recieve-map :- { (s/required-key :id) s/String
                                            (s/required-key :fn) s/Any}]
  {:pre [(fn? (:fn recieve-map))]}

  (add-to-generic :recieve-fns recieve-map))

(s/defn add-to-sendfns [send-map :- { (s/required-key :id) s/String
                                      (s/required-key :fn) s/Any}]
  {:pre [(fn? (:fn send-map))]}

  (add-to-generic :send-fns send-map))



;; CREATE Channels
(defn generate-channel
  ([] (generate-channel (str (java.util.UUID/randomUUID))))
  ([channelID]
     {:id channelID
      :channel (chan)}))

(defn generate-kernel-channel []
  (generate-channel "kernel-channel"))



;; GET a Channel
(defn get-channel [ID]
  (->> (:channel-list @*SYSTEM*) (filter #(= ID (:id %))) first))

(defn get-kernel-channel []
  (get-channel "kernel-channel"))



;; LOAD Config information
(defn load-config-raw []
  (load-string (slurp (io/resource "config.edn"))))

(def load-config (memoize load-config-raw))


(defn get-system [] *SYSTEM*)
(defn get-domain []
  (:domain @(get-system)))

(defn get-domain-schema []
  {:posts (domain/post-schema)
   :assets (domain/asset-schema)
   :tags (domain/tag-schema)})

(defn get-posts []
  (-> @(get-system) :domain :posts))

(defn get-assets []
  (-> @(get-system) :domain :assets))

(defn get-tags []
  (-> @(get-system) :domain :tags))



;; PLUGIN Handling
(defn attach-plugin [handlerfn]

  ;; plugin gets 1 send fn and 1 recieve fn
  (let [new-channel (generate-channel)
        kernel-send (plugin/generate-send-fn (:channel new-channel))

        sendfn (plugin/generate-send-fn (:channel (get-kernel-channel)))
        recievefn (plugin/generate-recieve-fn (:channel new-channel))
        xx (recievefn handlerfn)]

    ;; KERNEL binding
    (add-to-sendfns {:id (:id new-channel) :fn kernel-send})

    ;; PLUGIN binding
    {:id (:id new-channel)
     :sendfn sendfn
     :recievefn recievefn}))


(defn send-message-raw [idlist message]

  ;;(select-keys {:fu :bar :qwerty "asdf" :thing 2} '(:thing :a :b :fu))
  ;;(some #{:posts :assets :tags} (keys (:domain system)))

  (let [all-send-ids (map :id (:send-fns @*SYSTEM*))

        filtered-sends (filter #(some #{(:id %)} idlist)
                               (:send-fns @*SYSTEM*))]

    (reduce (fn [rslt echf]
              ((:fn echf) message))
            []
            filtered-sends)))

(defn send-message
  ([message] (send-message {:include :all} message))
  ([conditions message]

     #_(if-not conditions)))


(s/defn kernel-handler
    "Goes through all the keys and passes associated values to system mapped action. Event structures should look like below. Full mappings can be found in resources/config.edn.

     {:stefon.post.create {:parameters {:title \"Latest In Biotechnology\" :content \"Lorem ipsum.\" :created-date \"0000\" }}}"
  [message :- {(s/required-key :id) s/String
               (s/required-key :message) s/Any}]

  (println (str ">> kernel-handler CALLED > " message))

  (let [action-config (:action-mappings (load-config))
        action-keys (keys action-config)

        eventF (:message message)
        sendF (:send-handler message)
        filtered-event-keys (keys (select-keys eventF action-keys))]

    (println ">> RECOGNIZE? > " filtered-event-keys)

    (if filtered-event-keys
      nil
      nil)


    ;; ====
    ;; perform actions, based on keys
    ;;(println (str ">> filtered-event-keys[" filtered-event-keys "] / action-config[" action-config "]"))
    #_(reduce (fn [rslt ekey]

              (let [afn (ekey action-config)
                    params (-> eventF ekey :parameters vals)]


                ;; TODO - operations should occur in 1 place.. so data doesn't separate

                ;; execute the mapped action
                (println (str ">> execute on key[" ekey "] / payload[" `(~afn ~@params) "]"))
                (let [eval-result (eval `(~afn ~@params) )]

                  ;; send evaluation result back to sender
                  (sendF eval-result))

                ;; notify other plugins what has taken place; replacing :stefon... with :plugin...
                #_(send-message {(keyword (string/replace (name ekey) #"stefon" "plugin"))
                                 {:parameters (-> eventF ekey :parameters)}})))
            []
            filtered-event-keys)

    ;; ====
    ;; pass along any event(s) for which we do not have mappings
    #_(let [event-less-known-mappings (eval `(~dissoc ~eventF ~@action-keys))]

      (if-not (empty? event-less-known-mappings)

        (do (println (str ">> forwarding unknown events > " event-less-known-mappings))
            #_(send-message event-less-known-mappings))))))



;; START System
(defn start-system
  ([] (start-system @*SYSTEM* kernel-handler))
  ([system khandler]

     ;; CREATE System
     (swap! *SYSTEM* (fn [inp] (generate-system)))

     ;; Kernel CHANNEL
     (add-to-channel-list (generate-kernel-channel))

     ;; Kernel RECIEVEs
     (let [krecieve (plugin/generate-recieve-fn (:channel (get-kernel-channel)))
           xx (krecieve khandler)
           xx (add-to-recievefns {:id (:id (get-kernel-channel))
                                  :fn krecieve})])))



;; Posts
#_(defn create-post [title content content-type created-date modified-date assets tags]
  (functions/create *SYSTEM* :posts 'stefon.domain.Post title content content-type created-date modified-date assets tags))
#_(defn retrieve-post [ID] (functions/retrieve *SYSTEM* :posts ID))
#_(defn update-post [ID update-map] (functions/update *SYSTEM* :posts ID update-map))
#_(defn delete-post [ID] (functions/delete *SYSTEM* :posts ID))
#_(defn find-posts [param-map] (functions/find *SYSTEM* :posts param-map))
#_(defn list-posts [] (functions/list *SYSTEM* :posts))


;; Assets
#_(defn create-asset [name type asset] (functions/create *SYSTEM* :assets 'stefon.domain.Asset name type asset))
#_(defn retrieve-asset [ID] (functions/retrieve *SYSTEM* :assets ID))
#_(defn update-asset [ID update-map] (functions/update *SYSTEM* :assets ID update-map))
#_(defn delete-asset [ID] (functions/delete *SYSTEM* :assets ID))
#_(defn find-assets [param-map] (functions/find *SYSTEM* :assets param-map))
#_(defn list-assets [] (functions/list *SYSTEM* :assets))


;; Tags
#_(defn create-tag [name] (functions/create *SYSTEM* :tags 'stefon.domain.Tag name))
#_(defn retrieve-tag [ID] (functions/retrieve *SYSTEM* :tags ID))
#_(defn update-tag [ID update-map] (functions/update *SYSTEM* :tags ID update-map))
#_(defn delete-tag [ID] (functions/delete *SYSTEM* :tags ID))
#_(defn find-tags [param-map] (functions/find *SYSTEM* :tags param-map))
#_(defn list-tags [] (functions/list *SYSTEM* :tags))
