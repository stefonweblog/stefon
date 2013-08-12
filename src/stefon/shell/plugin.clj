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

  ;; attach plugin's handler
  (lamina/receive-all (:channel-sink system) receive-handler)

  ;; return a sender function
  (fn [^clojure.lang.PersistentHashMap event]

    (let [result-promise (promise)
          send-handler (fn [result-event]
                         (deliver result-promise result-event))]

      ;; pass the event, and a callback function, used to fill a promise
      (lamina/enqueue (:channel-sink system) {:send-event event :send-handler send-handler})
      result-promise)))

(defn publish-event
  "This function, internally, lets the core system pass messages to attached plugins"
  [system ^clojure.lang.PersistentHashMap event]

  (lamina/enqueue (:channel-spout system) event))
