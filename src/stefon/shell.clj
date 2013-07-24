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
(defn create [dkey klass & args]

  (let [uuidS (str (uuid/make-random))
        entity-record (eval `(new ~klass ~uuidS ~@args))]

    (swap! *SYSTEM* update-in [dkey] conj entity-record)
    entity-record))

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
  "Applies key value searching using an AND condition"
  [dkey param-map]

  (let [entries (seq param-map)]

    (seq (set/join [param-map] (dkey @*SYSTEM*)))))

(defn list [dkey]

  (dkey @*SYSTEM*))


;; Posts
(defn create-post [title content created-date]
  (create :posts 'stefon.domain.Post title content created-date))

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


;; Assets
(defn create-asset [asset type]
  (create :assets 'stefon.domain.Asset asset type))

(defn retrieve-asset [ID]
  (retrieve :assets ID))

(defn update-asset [ID update-map]
  (update :assets ID update-map))

(defn delete-asset [ID]
  (delete :assets ID))

(defn find-assets [param-map]
  (find :assets param-map))

(defn list-assets []
  (list :assets))


;; Tags
(defn create-tag [name]
  (create :tags 'stefon.domain.Tag name))

(defn retrieve-tag [ID]
  (retrieve :tags ID))

(defn update-tag [ID update-map]
  (update :tags ID update-map))

(defn delete-tag [ID]
  (delete :tags ID))

(defn find-tags [param-map]
  (find :tags param-map))

(defn list-tags []
  (list :tags))
