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

(defn get-kernel-channel []
  (get-channel "kernel-channel"))


;; SEND & Recieve Functions on a channel
(defn generate-send-fn [chanl]
  (fn [msg]
    (go (>! chanl msg))))

(defn generate-recieve-fn [chanl]
  (fn [handlefn]
    (go (loop [msg (<! chanl)]
          (handlefn msg)
          (recur (<! chanl))))))
