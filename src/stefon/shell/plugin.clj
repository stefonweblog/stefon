(ns stefon.shell.plugin

  (:require [lamina.core :as lamina]))


(defn create-plugin-system
  "Returns a System with 2 bi-directional channels.
   See reference here: https://github.com/ztellman/lamina/wiki/Connections"
  [system-map]

  {:pre [(map? system-map)]}

  (let [client-server (lamina/channel-pair)]
    (assoc system-map :channel-spout (first client-server) :channel-sink (second client-server))))


(defn close-plugin-channel [system-atom]
  (lamina/force-close (:channel-spout @system-atom)))

(defn attach-plugin
  "This function returns takes 1 function,
   receieve-handler: a function called when the plugin receives a message

   And returns another function,
   @returns: a function to invoke when the plugin needs to send the system a message"
  [system-atom receive-handler]

  {:pre [(-> system-atom nil? not)
         (= clojure.lang.Atom (type system-atom))
         (= clojure.lang.PersistentArrayMap (type @system-atom))

         (-> receive-handler nil? not)
         (-> receive-handler fn?)]}

  (lamina/receive-all (:channel-sink @system-atom) receive-handler)
  (fn [^clojure.lang.PersistentHashMap event]
    (lamina/enqueue (:channel-sink @system-atom))))

(defn publish-event
  "This function, internally, lets the core system pass messages to attached plugins"
  [system-atom ^clojure.lang.PersistentHashMap event]

  (lamina/enqueue (:channel-spout @system-atom) event))
