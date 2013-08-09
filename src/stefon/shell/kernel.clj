(ns stefon.shell.kernel

  (:require [clojure.string :as string]
            [clojure.core.async :as async]))


(declare get-kernel-channel)


;; KERNEL message handling
(defn init-kernel-channel [system-atom]

  ;; ensure we are getting a system atom with a hash embedded
  {:pre [(= clojure.lang.Atom (type system-atom))
         (= clojure.lang.PersistentArrayMap (type @system-atom))]}


  ;; ensure we don't already have an existing channel
  (if (not (and
            (not (nil? (get-kernel-channel system-atom)))
            (= clojure.core.async.impl.channels.ManyToManyChannel (type (get-kernel-channel system-atom)))))

    (swap! system-atom (fn [inp]
                         (assoc inp :kernel-channel (async/chan)))))
  system-atom)


(defn get-kernel-channel [system-atom]
  (:kernel-channel @system-atom))
