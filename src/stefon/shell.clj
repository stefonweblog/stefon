(ns stefon.shell

  (:require [stefon.shell.kernel :as kernel]))


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
  (kernel/attach-plugin handlerfn))
