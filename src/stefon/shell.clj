(ns stefon.shell

  (:require [clojure.set :as set]
            [stefon.domain :as domain]
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
(defn create [dkey title content created-date]

  (let [uuidS (str (uuid/make-random))
        post (stefon.domain.Post. uuidS title content created-date)]

    (swap! *SYSTEM* update-in [dkey] conj post)
    post))

(defn retrieve [dkey ID]

  (first
   (filter #(= (:id %) ID) (dkey @*SYSTEM*))))

(defn update [dkey ID update-map]

  {:pre (map? update-map)}

  (let [indexed-entry (seq (first (filter #(= (-> % second :id) ID)
                                          (map-indexed (fn [idx itm] [idx itm]) (dkey @*SYSTEM*)))))]

    (swap! *SYSTEM*
           update-in
           [dkey (first indexed-entry)]
           (fn [inp]

             (into inp update-map)))))

(defn delete [dkey ID]

  (swap! *SYSTEM* update-in [dkey] (fn [inp]
                                       (remove #(= (:id %) ID) inp))))

(defn find
  "Applies key value searching using an OR condition"
  [dkey param-map]

  (let [entries (seq param-map)]

    (seq (set/join [param-map] (dkey @*SYSTEM*)))))

(defn list [dkey]

  (dkey @*SYSTEM*))





(defn create-post [title content created-date]

  (create :posts title content created-date))

(defn retrieve-post [ID]

  (retrieve :posts ID))

(defn update-post [ID update-map]

  (update :posts ID update-map))

(defn delete-post [ID]

  (delete :posts ID))

(defn find-posts [param-map]

  (find :posts param-map))

(defn list-posts []

  (list :posts))
