(ns stefon.core
  (:require [stefon.shell.kernel :as kernel]))

(defprotocol Lifecycle
  (init [_ system])
  (start [_ system])
  (stop [_ system]))

;; A Jig Component
(deftype Component [config]
  Lifecycle

  (init [_ system]
    (assoc system :stefon/system (kernel/generate-system)))

  (start [_ system]
    system)

  (stop [_ system]
    system))
