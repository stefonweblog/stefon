(ns stefon.core
  (:require jig)
  (:import (jig Lifecycle)))

;; A Jig Component
(deftype Component [config]
  Lifecycle

  (init [_ system]
    system)

  (start [_ system]
    system)

  (stop [_ system]
    system))
