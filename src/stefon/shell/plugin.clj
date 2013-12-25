(ns stefon.shell.plugin
  (:require [clojure.core.async :as async :refer :all]
            [schema.core :as s]
            [stefon.schema :as ss]))


(ss/turn-on-validation)


;; SEND & Recieve Functions on a channel
(defn generate-send-fn [chanl]
  (fn [msg]
    (println ">> generated send CALLED > " msg)
    (go (>! chanl msg))))


(defn generate-recieve-fn [chanl]
  (fn [system-atom handlefn]

    (println "sanity check: " system-atom)
    (go (loop [msg (<! chanl)]
          (handlefn nil msg)
          (recur (<! chanl))))))


;; Channel
(defn add-receive-tee [system-atom recievefn]
  (swap! system-atom (fn [inp]
                       (update-in inp [:steonf/system :tee-fns] (fn [ii] (into [] (conj ii recievefn)))))))

(defn- add-to-generic [system-atom lookup-key thing-to-add]
  (swap! system-atom (fn [inp]
                       (update-in inp
                                  [:stefon/system lookup-key]
                                  (fn [ii] (conj ii thing-to-add))))))

(defn get-channel [system-atom ID]

  (->> @system-atom
       :stefon/system
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


(s/defn generate-channel
  ([] (generate-channel (str (java.util.UUID/randomUUID))))
  ([channelID :- s/String]
     {:id channelID
      :channel (chan)}))

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
        xx (recievefn system-atom handlerfn)]

    ;; KERNEL binding
    (swap! system-atom (fn [inp]
                         (update-in inp
                                    [:stefon/system :send-fns]
                                    (fn [ii]
                                      (conj ii
                                            {:id (:id new-channel)
                                             :fn kernel-send})))))

    ;; TODO - send-fns and channels are related, but exist in 2 lists
    (swap! system-atom (fn [inp]
                         (update-in inp
                                    [:stefon/system :channel-list]
                                    (fn [ii] (conj ii new-channel)))))

    ;; PLUGIN binding
    (assoc new-channel :sendfn sendfn :recievefn recievefn)))
