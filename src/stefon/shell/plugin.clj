(ns stefon.shell.plugin

  (:require [lamina.core :as lamina]))


(defn create-plugin-system [system-map]

  {:pre [(map? system-map)]}

  (let [client-server (lamina/channel-pair)]


    (assoc system-map :channel-spout (first client-server) :channel-sink (second client-server))))
