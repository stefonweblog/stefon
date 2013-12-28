(ns stefon.shell.plugin
  (:require [clojure.core.async :as async]
            [schema.core :as s]
            [stefon.schema :as ss]))


(ss/turn-on-validation)


;; SEND & Recieve Functions on a channel
(defn generate-send-fn [chanl]
  (fn [msg]
    (async/go (async/>! chanl msg))))

(defn generate-recieve-fn [chanl]
  (fn [system-atom handlefn]
    (async/go-loop  [msg (async/<! chanl) satom system-atom]

                    #_(println "2: handlefn[" handlefn "] msg[" msg "] satom[" satom "]")
                    (handlefn satom msg)
                    (recur (async/<! chanl) satom))))


;; Channel
(defn add-receive-tee [system-atom recievefn]
  (swap! system-atom (fn [inp]
                       (update-in inp [:steonf/system :tee-fns] (fn [ii] (into [] (conj ii recievefn)))))))

(defn- add-to-generic [system-atom lookup-key thing-to-add]
  (swap! system-atom (fn [inp]
                       (update-in inp
                                  [lookup-key]
                                  (fn [ii] (conj ii thing-to-add))))))

(defn get-channel [system-atom ID]

  (->> @system-atom
       :channel-list
       (filter #(= ID (:id %)))
       first))

(defn get-kernel-channel [system-atom]
  (get-channel system-atom "kernel-channel"))

(s/defn add-to-channel-list
  [system-atom new-channel :- { (s/required-key :id) s/String
                                (s/required-key :channel) s/Any}]
  (add-to-generic system-atom :channel-list new-channel))

(s/defn add-to-recievefns [system-atom recieve-map :- { (s/required-key :id) s/String
                                                        (s/required-key :fn) s/Any}]
  {:pre [(fn? (:fn recieve-map))]}

  (add-to-generic system-atom :recieve-fns recieve-map))

(s/defn add-to-sendfns [system-atom send-map :- { (s/required-key :id) s/String
                                                  (s/required-key :fn) s/Any}]
  {:pre [(fn? (:fn send-map))]}

  (add-to-generic system-atom :send-fns send-map))


(s/defn generate-channel
  ([] (generate-channel (str (java.util.UUID/randomUUID))))
  ([channelID :- s/String]
     {:id channelID
      :channel (async/chan)}))

(defn generate-kernel-channel []
  (generate-channel "kernel-channel"))

(defn generate-kernel-recieve [system-atom khandler]

  (let [krecieve (generate-recieve-fn (:channel (get-kernel-channel system-atom)))]
    (krecieve system-atom khandler)
    (add-to-recievefns system-atom
                       {:id (:id (get-kernel-channel system-atom))
                        :fn krecieve}) ))


;; PLUGIN Handling
(defn attach-plugin [system-atom handlerfn]

  ;; plugin gets 1 send fn and 1 recieve fn
  (let [new-channel (generate-channel)
        kernel-send (generate-send-fn (:channel new-channel))

        sendfn (generate-send-fn (:channel (get-kernel-channel system-atom)))
        recievefn (generate-recieve-fn (:channel new-channel))
        x (recievefn system-atom handlerfn)]

    ;; KERNEL binding
    (add-to-recievefns system-atom {:id (:id new-channel) :fn kernel-send})
    (add-to-sendfns system-atom {:id (:id new-channel) :fn kernel-send})
    (add-to-channel-list system-atom new-channel)

    ;; PLUGIN binding
    (assoc new-channel :sendfn sendfn :recievefn recievefn)


    ;; TODO - send-fns and channels are related, but exist in 2 lists
    #_(swap! system-atom (fn [inp]
                         (update-in inp
                                    [:stefon/system :send-fns]
                                    (fn [ii]
                                      (conj ii
                                            {:id (:id new-channel)
                                             :fn kernel-send})))))
    #_(swap! system-atom (fn [inp]
                         (update-in inp
                                    [:stefon/system :channel-list]
                                    (fn [ii] (conj ii new-channel)))))))
