(ns stefon.shell.kernel-process
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as string]
            [taoensso.timbre :as timbre]
            [schema.core :as s]
            [stefon.shell.kernel-crud :as kcrud]))


(defn load-config-raw []
  (load-string (slurp (io/resource "config.edn"))))

(def load-config (memoize load-config-raw))


(defn send-message-raw [system-atom idlist message]

  (timbre/debug ">> send-message-raw CALLED > idlist [" idlist "] > message [" message "]")
  (let [all-send-ids (map :id (:send-fns @system-atom))

        filtered-sends (filter #(some #{(:id %)} idlist)
                               (-> @system-atom :send-fns))]

    (reduce (fn [rslt echf] ((:fn echf) message))
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

     (let [all-send-ids (map :id (-> @system-atom :send-fns))

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
                                                      (into #{} exclude)))))]

       (send-message-raw system-atom filtered-list message))))


;; TODO hanve a way to enforce parameter ordering for function parameters
(s/defn process-original-message
  [system-atom
   action-keys
   action-config
   message :- {(s/required-key :id) s/String
               (s/required-key :message) s/Any}]

  (let [incoming-message (:message message)

        ;; FILTER known message(s)
        filtered-event-keys (keys (select-keys incoming-message action-keys))]

    ;; DO
    (if filtered-event-keys

      ;; yes
      (let [process-fn (fn [rslt each-key]

                         (let [afn (each-key action-config)
                               params (->> incoming-message each-key :parameters vals (cons system-atom))]

                           (timbre/debug ">> execute command > afn[" afn "] > params[" params "]")

                           ;; EXECUTE the mapped action
                           (let [eval-result
                                 (try (apply @(resolve afn) params)
                                      (catch Exception e (timbre/error "Exception: " (.getMessage e))))]

                             (timbre/debug ">> execute result [" eval-result "] / ID ["
                                      (:id message) "] / message [" message "]")

                             ;; SEND evaluation result back to sender
                             (send-message system-atom
                                           {:include [(:id message)]}
                                           {:from "kernel"
                                            :action each-key
                                            :result eval-result
                                            :message-id (-> message :message :message-id)})

                             (timbre/info ">> notify plugins > 1[" (-> message :message) "] > 2[" (-> message :message each-key) "] > 3[" (-> message :message each-key :parameters) "]")

                             ;; NOTIFY other plugins what has taken place;
                             ;;  replacing :stefon... with :plugin...
                             (send-message system-atom
                                           {:exclude [(:id message)]}
                                           (if-not (= :stefon.post.find each-key)
                                             {
                                              (keyword (string/replace (name each-key) #"stefon" "plugin"))
                                              {:id (:id message)
                                               :message {each-key
                                                         {:parameters
                                                          (merge (-> message :message each-key :parameters)
                                                                 eval-result)}
                                                         :message-id (-> message :message :message-id)}}
                                              }
                                             {
                                              (keyword (string/replace (name each-key) #"stefon" "plugin"))
                                              message
                                              })))))]
        (if (= 1 (count filtered-event-keys))
          (process-fn [] (first filtered-event-keys))
          (reduce process-fn [] filtered-event-keys)))

      ;; no
      (send-message system-atom
                    {:exclude [(:id message)]}
                    message))))


(s/defn process-result-message
  [system-atom message :- {(s/required-key :id) s/String
                                                        (s/required-key :origin) s/String
                                                        (s/required-key :action) s/Keyword
                                                        (s/required-key :result) s/Any}]

  (timbre/debug "process-result-message CALLED")
  (send-message system-atom
                {:include [(:origin message)]}
                {:from (:id message)
                 :origin (:origin message) :action (:action message) :result (:result message)}))


(s/defn kernel-handler
    "Goes through all the keys and passes associated values to system mapped action. Event structures should look like below. Full mappings can be found in resources/config.edn.

     An original message can be sent with A. A response can be sent with B.

     A) {:id plugin1 :message {:stefon.post.create {:parameters {:title \"Latest In Biotechnology\" :content \"Lorem ipsum.\" :created-date \"0000\" }}}}
     B) {:id plugin2 :origin plugin1 :result {:fu :bar}}"
    [system-atom message :- (s/either {(s/required-key :id) s/String
                                       (s/required-key :message) s/Any}
                                      {(s/required-key :id) s/String
                                       (s/required-key :origin) s/String
                                       (s/required-key :action) s/Keyword
                                       (s/required-key :result) s/Any})]

    (timbre/debug ">> kernel-handler CALLED > " message)

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
