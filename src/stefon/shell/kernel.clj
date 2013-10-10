(ns stefon.shell.kernel

  (:require [clojure.java.io :as io]
            [clojure.set :as set]
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
  {:domain {:posts []
            :assets []
            :tags []}
   :channel-list []

   :send-fns []
   :recieve-fns []

   :tee-fns []})


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

;; Useful for testing purposes - get alerted when plugin receives a message
(defn add-receive-tee [recievefn]
  (swap! *SYSTEM* (fn [inp]
                    (update-in inp [:tee-fns] (fn [ii] (into [] (conj ii recievefn)))))))



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
  (-> @(get-system) :domain))

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



;; MESSAGE Handling
(defn send-message-raw [idlist message]


  ;;(println ">> send-message-raw CALLED > idlist [" idlist "] > message [" message "]")

  (let [all-send-ids (map :id (:send-fns @*SYSTEM*))

        filtered-sends (filter #(some #{(:id %)} idlist)
                               (:send-fns @*SYSTEM*))]

    (reduce (fn [rslt echf]

              ;;(println ">> calling fn > " (:fn echf))
              ((:fn echf) message))
            []
            filtered-sends)))

(defn send-message
  "Send a message to attached plugins. You can pass in conditions with:
   {:include [ :all || [ id1 id2 ] ]}
   {:exclude [ :all || [ id1 id2 ] ]}"
  ([message] (send-message {:include :all} message))
  ([conditions message]

     ;; only :include || :exclude
     {:pre [(map? conditions)
            (set/subset? (keys conditions) #{:include :exclude})]}

     (let [all-send-ids (map :id (:send-fns @*SYSTEM*))

           ;; INCLUDE
           include (:include conditions)

           ;; EXCLUDE
           exclude (:exclude conditions)

           filtered-list (into [] (if include
                                    (if (= :all include)
                                      all-send-ids
                                      (set/intersection (into #{} all-send-ids)
                                                        (into #{} include)))
                                    (if (= :all exclude)
                                      []
                                      (set/difference (into #{} all-send-ids)
                                                      (into #{} exclude)))))
           ]

       (send-message-raw filtered-list message))))


(s/defn process-original-message [action-keys action-config message :- {(s/required-key :id) s/String
                                                                        (s/required-key :message) s/Any}]

  (let [eventF (:message message)

        ;; FILTER known message(s)
        filtered-event-keys (keys (select-keys eventF action-keys))]


    ;; DO
    (if filtered-event-keys

      ;; yes
      (reduce (fn [rslt ekey]

                (let [afn (ekey action-config)
                      params (-> eventF ekey :parameters vals)]

                  ;;(println ">> execute command [" afn "] > params [" params "]")

                  ;; EXECUTE the mapped action
                  (let [eval-result (eval `(~afn ~@params) )]

                    ;;(println ">> execute result [" eval-result "] / ID [" (:id message) "] / message [" message "]")

                    ;; SEND evaluation result back to sender
                    (send-message {:include [(:id message)]}
                                  {:from "kernel" :action ekey :result eval-result})


                    ;; NOTIFY other plugins what has taken place; replacing :stefon... with :plugin...
                    (send-message {:exclude [(:id message)]}
                                  (if-not (= :stefon.post.find ekey)
                                    {
                                     (keyword (string/replace (name ekey) #"stefon" "plugin"))
                                     {:id (:id message) :message {ekey {:parameters (merge (-> message :message ekey :parameters) eval-result)}}}
                                     }
                                    {
                                     (keyword (string/replace (name ekey) #"stefon" "plugin"))
                                     message
                                     })))))
              []
              filtered-event-keys)

      ;; no
      (send-message {:exclude [(:id message)]}
                    message))))


(s/defn process-result-message [message :- {(s/required-key :id) s/String
                                            (s/required-key :origin) s/String
                                            (s/required-key :action) s/Keyword
                                            (s/required-key :result) s/Any}]

  (send-message {:include [(:origin message)]}
                {:from (:id message) :origin (:origin message) :action (:action message) :result (:result message)}))


(s/defn kernel-handler
    "Goes through all the keys and passes associated values to system mapped action. Event structures should look like below. Full mappings can be found in resources/config.edn.

     An original message can be sent with A. A response can be sent with B.

     A) {:id plugin1 :message {:stefon.post.create {:parameters {:title \"Latest In Biotechnology\" :content \"Lorem ipsum.\" :created-date \"0000\" }}}}
     B) {:id plugin2 :origin plugin1 :result {:fu :bar}}"
    [message :- (s/either {(s/required-key :id) s/String
                           (s/required-key :message) s/Any}
                          {(s/required-key :id) s/String
                           (s/required-key :origin) s/String
                           (s/required-key :action) s/Keyword
                           (s/required-key :result) s/Any})]


    (println ">> kernel-handler CALLED > " message)

    ;; NOTIFY tee-fns
    (reduce (fn [rslt echF]
              (echF message)
              rslt)
            []
            (:tee-fns @*SYSTEM*))


    (let [action-config (:action-mappings (load-config))
          action-keys (keys action-config)]

      (if-not (= '(:id :origin :action :result) (keys message))

        ;; original messages
        (process-original-message action-keys action-config message)

        ;; response messages
        (process-result-message message))))


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
                                  :fn krecieve})]

       ;; load 3rd party PLUGINs
       (let [plugin-list (:plugins (load-config))]

         (reduce (fn [rslt ech]

                   #_(println "... " ech)

                   #_(require (symbol (str (name ech) ".plugin")))

                   #_(let [pfn (get (ns-publics (find-ns ech)) 'plugin)]

                     (println ">> plugin-fn > " pfn)))
                 []
                 plugin-list)
         )

       {:system-started? (fn []
                           (if-not (nil? @(get-system))
                             true
                             false))
        :start-system start-system
        :attach-plugin (fn [] plugin/attach-plugin *SYSTEM* khandler)}

       *SYSTEM*)))

(defn stop-system []
  (swap! *SYSTEM* (fn [inp] nil)))


;; Posts
(defn create-post [title content content-type created-date modified-date assets tags]
  (functions/create *SYSTEM* :posts 'stefon.domain.Post title content content-type created-date modified-date assets tags))
(defn create-relationship [entity-list] )  ;; presently a noop
(defn retrieve-post [ID] (functions/retrieve *SYSTEM* :posts ID))
(defn update-post [ID update-map] (functions/update *SYSTEM* :posts ID update-map))
(defn delete-post [ID] (functions/delete *SYSTEM* :posts ID))
(defn find-posts [param-map] (functions/find *SYSTEM* :posts param-map))
(defn list-posts [] (functions/list *SYSTEM* :posts))


;; Assets
(defn create-asset [name type asset] (functions/create *SYSTEM* :assets 'stefon.domain.Asset name type asset))
(defn retrieve-asset [ID] (functions/retrieve *SYSTEM* :assets ID))
(defn update-asset [ID update-map] (functions/update *SYSTEM* :assets ID update-map))
(defn delete-asset [ID] (functions/delete *SYSTEM* :assets ID))
(defn find-assets [param-map] (functions/find *SYSTEM* :assets param-map))
(defn list-assets [] (functions/list *SYSTEM* :assets))


;; Tags
(defn create-tag [name] (functions/create *SYSTEM* :tags 'stefon.domain.Tag name))
(defn retrieve-tag [ID] (functions/retrieve *SYSTEM* :tags ID))
(defn update-tag [ID update-map] (functions/update *SYSTEM* :tags ID update-map))
(defn delete-tag [ID] (functions/delete *SYSTEM* :tags ID))
(defn find-tags [param-map] (functions/find *SYSTEM* :tags param-map))
(defn list-tags [] (functions/list *SYSTEM* :tags))
