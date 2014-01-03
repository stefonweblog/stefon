(ns stefon.shell.kernel-process
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as string]
            [schema.core :as s]
            [stefon.shell.kernel-crud :as kcrud]))


(defn load-config-raw []
  (load-string (slurp (io/resource "config.edn"))))

(def load-config (memoize load-config-raw))


(defn send-message-raw [system-atom idlist message]


  ;;(println ">> send-message-raw CALLED > idlist [" idlist "] > message [" message "]")

  (let [all-send-ids (map :id (:send-fns @system-atom))

        filtered-sends (filter #(some #{(:id %)} idlist)
                               (-> @system-atom :stefon/system :send-fns))]

    (reduce (fn [rslt echf]

              ;;(println ">> calling fn > " (:fn echf))
              ((:fn echf) message))
            []
            filtered-sends)))

(defn send-message
  "Send a message to attached plugins. You can pass in conditions with:
   {:include [ :all || [ id1 id2 ] ]}
   {:exclude [ :all || [ id1 id2 ] ]}"
  ([system-atom message] (send-message system-atom {:include :all} message))
  ([system-atom conditions message]

     ;; only :include || :exclude
     {:pre [(map? conditions)
            (set/subset? (keys conditions) #{:include :exclude})]}

     (let [all-send-ids (map :id (-> @system-atom :stefon/system :send-fns))

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

       (send-message-raw system-atom filtered-list message))))


(defn process-original-message

  #_[system-atom
     action-keys
     action-config
     message :- {(s/required-key :id) s/String
                 (s/required-key :message) s/Any}]

  [system-atom action-keys action-config message]

  (let [eventF (:message message)

        ;; FILTER known message(s)
        filtered-event-keys (keys (select-keys eventF action-keys))]

    ;; DO
    (if filtered-event-keys

      ;; yes
      (let [process-fn (fn [rslt ekey]

                         (let [afn (ekey action-config)
                               params (->> eventF ekey :parameters vals (cons system-atom))]

                           (println ">> execute command [" afn "] > params [" params "]")

                           ;; EXECUTE the mapped action
                           (let [eval-result (eval `(~afn ~@params))]

                             (println ">> execute result [" eval-result "] / ID ["
                                      (:id message) "] / message [" message "]")

                             ;; SEND evaluation result back to sender
                             (send-message system-atom
                                           {:include [(:id message)]}
                                           {:from "kernel" :action ekey :result eval-result})

                             ;; NOTIFY other plugins what has taken place;
                             ;;  replacing :stefon... with :plugin...
                             (send-message system-atom
                                           {:exclude [(:id message)]}
                                           (if-not (= :stefon.post.find ekey)
                                             {
                                              (keyword (string/replace (name ekey) #"stefon" "plugin"))
                                              {:id (:id message)
                                               :message {ekey {:parameters
                                                               (merge (-> message :message ekey :parameters)
                                                                      eval-result)}}}
                                              }
                                             {
                                              (keyword (string/replace (name ekey) #"stefon" "plugin"))
                                              message
                                              })))))]
        (if (= 1 (count filtered-event-keys))
          (process-fn [] (first filtered-event-keys))
          (reduce process-fn [] filtered-event-keys)))

      ;; no
      (send-message system-atom
                    {:exclude [(:id message)]}
                    message))))


(defn process-result-message
  #_[system-atom message :- {(s/required-key :id) s/String
                                                        (s/required-key :origin) s/String
                                                        (s/required-key :action) s/Keyword
                                                        (s/required-key :result) s/Any}]
  [system-atom message]

  (send-message system-atom
                {:include [(:origin message)]}
                {:from (:id message)
                 :origin (:origin message) :action (:action message) :result (:result message)}))


(defn kernel-handler2 []
  (fn [one two]
    (println "kernel-handler2 CALLED > one[" one "] > two[" two "]")))

(defn kernel-handler
    "Goes through all the keys and passes associated values to system mapped action. Event structures should look like below. Full mappings can be found in resources/config.edn.

     An original message can be sent with A. A response can be sent with B.

     A) {:id plugin1 :message {:stefon.post.create {:parameters {:title \"Latest In Biotechnology\" :content \"Lorem ipsum.\" :created-date \"0000\" }}}}
     B) {:id plugin2 :origin plugin1 :result {:fu :bar}}"
    [system-atom message]
    #_[system-atom message :- (s/either {(s/required-key :id) s/String
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
            (:tee-fns @system-atom))


    (let [action-config (:action-mappings (load-config))
          action-keys (keys action-config)]

      (if-not (= '(:id :origin :action :result) (keys message))

        ;; original messages
        (process-original-message system-atom action-keys action-config message)

        ;; response messages
        (process-result-message system-atom message))))

(defn get-kernel-handler []
  kernel-handler)
