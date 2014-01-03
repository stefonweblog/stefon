(ns stefon.shell.plugin
  (:require [clojure.core.async :as async]
            [schema.core :as s]
            [stefon.schema :as ss]))


(ss/turn-on-validation)


;; SEND & Receive Functions on a channel
(defn generate-send-fn [chanl]
  (fn [msg]
    (async/go (async/>! chanl msg))))

(defn generate-receive-fn [chanl]
  (fn [system-atom handlefn]
    (async/go-loop  [msg (async/<! chanl) satom system-atom]

                    (handlefn satom msg)
                    (recur (async/<! chanl) satom))))


;; Channel
(defn add-receive-tee [system-atom receivefn]
  (swap! system-atom (fn [inp]
                       (update-in inp [:steonf/system :tee-fns] (fn [ii] (into [] (conj ii receivefn)))))))

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

(s/defn add-to-receivefns [system-atom receive-map :- { (s/required-key :id) s/String
                                                        (s/required-key :fn) s/Any}]
  {:pre [(fn? (:fn receive-map))]}

  (add-to-generic system-atom :receive-fns receive-map))

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

(defn generate-kernel-receive [system-atom khandler]

  (let [kreceive (generate-receive-fn (:channel (get-kernel-channel system-atom)))]
    (kreceive system-atom khandler)
    (add-to-receivefns system-atom
                       {:id (:id (get-kernel-channel system-atom))
                        :fn kreceive}) ))


;; PLUGIN Handling
(defn attach-plugin [system-atom handlerfn]

  ;; plugin gets 1 send fn and 1 receive fn
  (let [new-channel (generate-channel)
        kernel-send (generate-send-fn (:channel new-channel))

        sendfn (generate-send-fn (:channel (get-kernel-channel system-atom)))
        receivefn (generate-receive-fn (:channel new-channel))
        x (receivefn system-atom handlerfn)]

    ;; KERNEL binding
    (add-to-receivefns system-atom {:id (:id new-channel) :fn kernel-send})
    (add-to-sendfns system-atom {:id (:id new-channel) :fn kernel-send})
    (add-to-channel-list system-atom new-channel)

    ;; PLUGIN binding
    (assoc new-channel :sendfn sendfn :receivefn receivefn)))
