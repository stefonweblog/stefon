(ns stefon.shell.kernel-crud
  (:require [stefon.domain :as domain]
            [stefon.shell.functions :as functions]
            [stefon.shell.kernel :as kernel]))


(defn get-posts []
  (-> @(kernel/get-system) :stefon/system :domain :posts))

(defn get-assets []
  (-> @(kernel/get-system) :stefon/system :domain :assets))

(defn get-tags []
  (-> @(kernel/get-system) :stefon/system :domain :tags))

(defn get-domain-schema []
  {:posts (domain/post-schema)
   :assets (domain/asset-schema)
   :tags (domain/tag-schema)})


;; Posts
(defn create-post [title content content-type created-date modified-date assets tags]
  (functions/create
   (kernel/get-system)
   :posts 'stefon.domain.Post title content content-type created-date modified-date assets tags))
(defn create-relationship [entity-list])  ;; presently a noop
(defn retrieve-post [ID] (functions/retrieve (kernel/get-system) :posts ID))
(defn update-post [ID update-map] (functions/update (kernel/get-system) :posts ID update-map))
(defn delete-post [ID] (functions/delete (kernel/get-system) :posts ID))
(defn find-posts [param-map] (functions/find (kernel/get-system) :posts param-map))
(defn list-posts [] (functions/list (kernel/get-system) :posts))


;; Assets
(defn create-asset [name type asset]
  (functions/create (kernel/get-system) :assets 'stefon.domain.Asset name type asset))
(defn retrieve-asset [ID] (functions/retrieve (kernel/get-system) :assets ID))
(defn update-asset [ID update-map] (functions/update (kernel/get-system) :assets ID update-map))
(defn delete-asset [ID] (functions/delete (kernel/get-system) :assets ID))
(defn find-assets [param-map] (functions/find (kernel/get-system) :assets param-map))
(defn list-assets [] (functions/list (kernel/get-system) :assets))


;; Tags
(defn create-tag [name] (functions/create (kernel/get-system) :tags 'stefon.domain.Tag name))
(defn retrieve-tag [ID] (functions/retrieve (kernel/get-system) :tags ID))
(defn update-tag [ID update-map] (functions/update (kernel/get-system) :tags ID update-map))
(defn delete-tag [ID] (functions/delete (kernel/get-system) :tags ID))
(defn find-tags [param-map] (functions/find (kernel/get-system) :tags param-map))
(defn list-tags [] (functions/list (kernel/get-system) :tags))
