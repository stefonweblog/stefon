(ns stefon.shell

  (:require [stefon.shell.kernel :as kernel]
            [stefon.shell.plugin :as plugin]))


(defn create-system []
  (kernel/generate-system))

(defn system-started? []
  (if-not (nil? @(kernel/get-system))
    true
    false))

(defn start-system []
  (kernel/start-system))

(defn stop-system []
  (kernel/stop-system))

(defn attach-plugin [handlerfn]
  (plugin/attach-plugin kernel/*SYSTEM* handlerfn))
