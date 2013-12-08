(ns stefon.core
  (:require jig
            [stefon.shell.kernel :as kernel])
  (:import (jig Lifecycle)))

;; A Jig Component
(deftype Component [config]
  Lifecycle

  (init [_ system]
    (assoc system :stefon/system (kernel/generate-system)))

  (start [_ system]
    system)

  (stop [_ system]
    system))
