(ns stefon.shell.plugin

  (:require [lamina.core :as lamina]))


(defn create-plugin-system
  "Returns a System with 2 bi-directional channels.
   See reference here: https://github.com/ztellman/lamina/wiki/Connections"
  [system]

  {:pre [(map? system)]}

  (let [client-server (lamina/channel-pair)]
    (assoc system :channel-spout (first client-server) :channel-sink (second client-server))))


(defn close-plugin-channel [system]
  (lamina/force-close (:channel-spout system)))

(defn attach-plugin
  "This function returns takes 1 function,
   receieve-handler: a function called when the plugin receives a message

   And returns another function,
   @returns: a function to invoke when the plugin needs to send the system a message"
  [system receive-handler]

  {:pre [(-> system nil? not)
         (= clojure.lang.PersistentArrayMap (type system))

         (-> receive-handler nil? not)
         (-> receive-handler fn?)]}

  (lamina/receive-all (:channel-sink system) receive-handler)
  (fn [^clojure.lang.PersistentHashMap event]
    (lamina/enqueue (:channel-sink system) event)))

(defn publish-event
  "This function, internally, lets the core system pass messages to attached plugins"
  [system ^clojure.lang.PersistentHashMap event]

  (lamina/enqueue (:channel-spout system) event))
