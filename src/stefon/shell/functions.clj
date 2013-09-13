(ns stefon.shell.functions

  (:require [clojure.set :as set]
            [cljs-uuid.core :as uuid]
            [stefon.domain :as domain]))



;; ====
;; Functions for CRUD'ing to system structures
(defn create [system-atom dkey klass & args]

  (let [uuidS (str (uuid/make-random))
        entity-record (eval `(new ~klass ~uuidS ~@args))]

    (swap! system-atom update-in [:domain dkey] conj entity-record)
    entity-record))

(defn retrieve [system-atom dkey ID]

  (first
   (filter #(= (:id %) ID) (-> @system-atom :domain dkey))))

(defn update [system-atom dkey ID update-map]

  {:pre (map? update-map)}

  (let [indexed-entry (seq (first (filter #(= (-> % second :id) ID)
                                          (map-indexed (fn [idx itm] [idx itm]) (-> @system-atom :domain dkey)))))]

    (swap! system-atom
           update-in
           [:domain dkey (first indexed-entry)]
           (fn [inp]

             (into inp update-map)))))

(defn delete [system-atom dkey ID]

  (swap! system-atom update-in [:domain dkey] (fn [inp]
                                       (remove #(= (:id %) ID) inp))))

(defn find
  "Applies key value searching using an AND condition"
  [system-atom dkey param-map]

  (let [entries (seq param-map)]

    (seq (set/join [param-map] (-> @system-atom :domain dkey)))))

(defn list [system-atom dkey]

  (-> @system-atom :domain dkey))
