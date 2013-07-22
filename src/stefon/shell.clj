(ns stefon.shell

  (:require [stefon.domain :as domain]))


(def ^{:doc "In memory representation of the running system structures"}
  ^:dynamic *SYSTEM* (atom nil))

(defn create-system []

  {:posts []
   :assets []
   :tags []})

(defn start-system

  ([]
     (start-system (create-system)))

  ([system]

     ;; Setup the system atom
     (swap! *SYSTEM* (fn [inp] system))

     ;; Generate Post, Asset and Tag record types

     ;; switch namespaces
     (in-ns 'stefon.shell)))

(defn stop-system []

  (swap! *SYSTEM* (fn [inp]  nil))
  (in-ns 'user))


(defn create-post [title content created-date]

  (let [post (stefon.domain.Post. title content created-date)]

    (swap! *SYSTEM* update-in [:posts] conj post)))
