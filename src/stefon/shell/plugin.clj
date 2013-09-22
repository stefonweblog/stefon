(ns stefon.shell.plugin
  (:require [clojure.core.async :as async :refer :all]))


(declare channel-list)
(declare kernel-send)
(declare kernel-recieve)


(defn add-to-channel-list [new-channel]
  (swap! channel-list (fn [inp] (conj inp new-channel))))


;; CREATE Channels
(defn generate-channel
  ([]
     (generate-channel (str (java.util.UUID/randomUUID))))
  ([channelID]
     {:id channelID
      :channel (chan)}))

(defn generate-kernel-channel []
  (generate-channel "kernel-channel"))


;; GET a Channel
(defn get-channel [ID]
  (->> @channel-list (filter #(= ID (:id %))) first))

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
