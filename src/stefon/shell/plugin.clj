(ns stefon.shell.plugin
  (:require [clojure.core.async :as async :refer :all]
            [schema.core :as s]

            [stefon.schema :as ss]))


(ss/turn-on-validation)


;; CREATE Channels
(s/defn generate-channel
  ([]
     (generate-channel (str (java.util.UUID/randomUUID))))
  ([channelID :- s/String]
     {:id channelID
      :channel (chan)}))

(defn generate-kernel-channel []
  (generate-channel "kernel-channel"))


;; GET a Channel
(defn get-channel [channel-list ID]
  (->> channel-list (filter #(= ID (:id %))) first))

(defn get-kernel-channel [system-atom]
  (get-channel (-> @system-atom :stefon/system :channel-list) "kernel-channel"))


;; SEND & Recieve Functions on a channel
(defn generate-send-fn [chanl]
  (fn [msg]
    (go (>! chanl msg))))


(defn generate-recieve-fn [chanl]
  (fn [system-atom handlefn]

    (go (loop [msg (<! chanl)]

          #_(println ">> generated recieve CALLED > " msg)
          (handlefn system-atom msg)
          (recur (<! chanl))))))


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
                                    [:send-fns]
                                    (fn [ii]
                                      (into [] (conj ii
                                                     {:id (:id new-channel)
                                                      :fn kernel-send}))))))

    ;; TODO - send-fns and channels are related, but exist in 2 lists
    (swap! system-atom (fn [inp]
                         (update-in inp
                                    [:channel-list]
                                    (fn [ii]
                                      (into [] (conj ii new-channel))))))

    ;; PLUGIN binding
    (assoc new-channel :sendfn sendfn :recievefn recievefn)))
