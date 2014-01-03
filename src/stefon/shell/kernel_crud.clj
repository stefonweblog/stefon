(ns stefon.shell.kernel-crud
  (:require [stefon.domain :as domain]
            [stefon.shell.functions :as functions]))


(defn get-posts [system-atom]
  (-> @system-atom :stefon/system :domain :posts))

(defn get-assets [system-atom]
  (-> @system-atom :stefon/system :domain :assets))

(defn get-tags [system-atom]
  (-> @system-atom :stefon/system :domain :tags))

(defn get-domain-schema
  ([] (get-domain-schema nil))
  ([sistem-atom]
     {:posts (domain/post-schema)
      :assets (domain/asset-schema)
      :tags (domain/tag-schema)}))


;; Posts
(defn create-post [system-atom title content content-type created-date modified-date assets tags]
  (functions/create
   system-atom
   :posts 'stefon.domain.Post title content content-type created-date modified-date assets tags))
(defn create-relationship [system-atom entity-list])  ;; presently a noop
(defn retrieve-post [system-atom ID] (functions/retrieve system-atom :posts ID))
(defn update-post [system-atom ID update-map] (functions/update system-atom :posts ID update-map))
(defn delete-post [system-atom ID] (functions/delete system-atom :posts ID))
(defn find-posts [system-atom param-map] (functions/find system-atom :posts param-map))
(defn list-posts [system-atom ] (functions/list system-atom :posts))


;; Assets
(defn create-asset [system-atom name type asset]
  (functions/create system-atom :assets 'stefon.domain.Asset name type asset))
(defn retrieve-asset [system-atom ID] (functions/retrieve system-atom :assets ID))
(defn update-asset [system-atom ID update-map] (functions/update system-atom :assets ID update-map))
(defn delete-asset [system-atom ID] (functions/delete system-atom :assets ID))
(defn find-assets [system-atom param-map] (functions/find system-atom :assets param-map))
(defn list-assets [system-atom ] (functions/list system-atom :assets))


;; Tags
(defn create-tag [system-atom name] (functions/create system-atom :tags 'stefon.domain.Tag name))
(defn retrieve-tag [system-atom ID] (functions/retrieve system-atom :tags ID))
(defn update-tag [system-atom ID update-map] (functions/update system-atom :tags ID update-map))
(defn delete-tag [system-atom ID] (functions/delete system-atom :tags ID))
(defn find-tags [system-atom param-map] (functions/find system-atom :tags param-map))
(defn list-tags [system-atom ] (functions/list system-atom :tags))
