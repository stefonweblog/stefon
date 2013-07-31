(ns stefon.shell.kernel

  (:require [lamina.core :as lamina]))


(defn handle-incoming-messages [event]

  (println (str "handle-incoming-messages CALLED > " event)))


(defn attach-kernel
  "Attaches a listener / handler to an in coming lamina channel"
  [system]

  (lamina/receive-all (:channel-spout system) handle-incoming-messages))
