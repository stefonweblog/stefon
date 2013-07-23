(ns stefon.shell

  (:require [stefon.domain :as domain]
            [cljs-uuid.core :as uuid]))


;; ====
;; SYSTEM structure & functions
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



;; ====
;; Functions for CRUD'ing to system structures
(defn create-post [title content created-date]

  (let [uuidS (str (uuid/make-random))
        post (stefon.domain.Post. uuidS title content created-date)]

    (swap! *SYSTEM* update-in [:posts] conj post)
    post))

(defn retrieve-post [ID]

  (first
   (filter #(= (:id %) ID) (:posts @*SYSTEM*))))

(defn update-post [ID update-map]

  {:pre (map? update-map)}

  (let [indexed-entry (seq (first (filter #(= (-> % second :id) ID)
                                          (map-indexed (fn [idx itm] [idx itm]) (:posts @*SYSTEM*)))))]

    (swap! *SYSTEM*
           update-in
           [:posts (first indexed-entry)]
           (fn [inp]

             (into inp update-map)))))

(defn delete-post [ID]

  (swap! *SYSTEM* update-in [:posts] (fn [inp]
                                       (remove #(= (:id %) ID) inp))))
