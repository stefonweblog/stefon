(ns stefon.shell.kernel

  (:require [clojure.string :as string]
            [clojure.core.async :as async]))



;; KERNEL message handling
(defn init-kernel-channel [system-atom]

  ;; ensure we are getting a system atom with a hash embedded
  {:pre [(= clojure.lang.Atom (type system-atom))
         (= clojure.lang.PersistentArrayMap (type @system-atom))]}


  (swap! system-atom (fn [inp]
                       (assoc inp :kernel-channel (async/chan))))
  system-atom)


(defn get-kernel-channel [system-atom]
  (:kernel-channel @system-atom))
