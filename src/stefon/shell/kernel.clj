(ns stefon.shell.kernel

  (:require [clojure.java.io :as io]
            [lamina.core :as lamina]
            [clojure.string :as string]

            [stefon.domain :as domain]
            [stefon.shell.plugin :as plugin]
            [stefon.shell.functions :as functions]))



;; LOAD Config information
(defn load-config-raw []
  (load-string (slurp (io/resource "config.edn"))))

(def load-config (memoize load-config-raw))


;; SYSTEM structure & functions
(def  ^{:doc "In memory representation of the running system structures"}
      ^:dynamic *SYSTEM* (atom nil))


(defn get-system [] *SYSTEM*)

(defn get-domain []
  (:domain @(get-system)))

(defn get-domain-schema []
  {:posts (domain/post-schema)
   :assets (domain/asset-schema)
   :tags (domain/tag-schema)})

(defn get-posts []
  (-> @(get-system) :domain :posts))

(defn get-assets []
  (-> @(get-system) :domain :assets))

(defn get-tags []
  (-> @(get-system) :domain :tags))


(defn start-system [system kernel-handler]

  ;; Setup the system atom & attach plugin channels
  (swap! *SYSTEM* (fn [inp]

                    (let [with-plugin-system (plugin/create-plugin-system system)]
                      (attach-kernel with-plugin-system kernel-handler)
                      with-plugin-system))))



;; Posts
(defn create-post [title content content-type created-date modified-date assets tags]
  (functions/create *SYSTEM* :posts 'stefon.domain.Post title content content-type created-date modified-date assets tags))
(defn retrieve-post [ID] (functions/retrieve *SYSTEM* :posts ID))
(defn update-post [ID update-map] (functions/update *SYSTEM* :posts ID update-map))
(defn delete-post [ID] (functions/delete *SYSTEM* :posts ID))
(defn find-posts [param-map] (functions/find *SYSTEM* :posts param-map))
(defn list-posts [] (functions/list *SYSTEM* :posts))


;; Assets
(defn create-asset [name type asset] (functions/create *SYSTEM* :assets 'stefon.domain.Asset name type asset))
(defn retrieve-asset [ID] (functions/retrieve *SYSTEM* :assets ID))
(defn update-asset [ID update-map] (functions/update *SYSTEM* :assets ID update-map))
(defn delete-asset [ID] (functions/delete *SYSTEM* :assets ID))
(defn find-assets [param-map] (functions/find *SYSTEM* :assets param-map))
(defn list-assets [] (functions/list *SYSTEM* :assets))


;; Tags
(defn create-tag [name] (functions/create *SYSTEM* :tags 'stefon.domain.Tag name))
(defn retrieve-tag [ID] (functions/retrieve *SYSTEM* :tags ID))
(defn update-tag [ID update-map] (functions/update *SYSTEM* :tags ID update-map))
(defn delete-tag [ID] (functions/delete *SYSTEM* :tags ID))
(defn find-tags [param-map] (functions/find *SYSTEM* :tags param-map))
(defn list-tags [] (functions/list *SYSTEM* :tags))
