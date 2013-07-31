(ns stefon.shell.kernel

  (:require [lamina.core :as lamina]))


(defn handle-incoming-messages [event]

  (println (str "handle-incoming-messages CALLED > " event)))


(defn attach-kernel
  "Attaches a listener / handler to an in coming lamina channel"

  ([system]
     (attach-kernel system handle-incoming-messages))

  ([system message-handler]
     (lamina/receive-all (:channel-spout system) message-handler)))
