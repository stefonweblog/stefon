(ns stefon.shell
  (:require [lamina.core :as lamina]
            [stefon.domain :as domain]
            [stefon.shell.functions :as functions]
            [stefon.shell.plugin :as plugin]
            [stefon.shell.kernel :as kernel]))


(defn system-started? []

  (if-not (nil? @(kernel/get-system))
    true
    false))

(defn create-system
  "Creates a map with the core components of the system kernel"
  []

  {:domain {:posts []
            :assets []
            :tags []}
   :channel-spout nil
   :channel-sink nil})

(defn start-system
  ([] (start-system (create-system)))
  ([system] (start-system system kernel/handle-incoming-messages))
  ([system kernel-handler]

     ;; Delegate to the kernel
     (kernel/start-system system kernel-handler)

     ;; return *SYSTEM*
     (kernel/get-system)))

(defn stop-system []
  (swap! (kernel/get-system) (fn [inp]  nil))
  (in-ns 'user))


;; Open to a Stefon SHELL
(defn shell []

  (start-system)

  ;; switch namespaces
  (in-ns 'stefon.shell))


;; SUBSCRIPTION code
(defn close-plugin-channel []
  (plugin/close-plugin-channel @(kernel/get-system)))

(defn attach-plugin [receive-handler]

  (if (system-started?)
    (plugin/attach-plugin @(kernel/get-system) receive-handler)
    (throw Exception "System has not been started")))

(defn- publish-event [^clojure.lang.PersistentHashMap event]
  (plugin/publish-event @(kernel/get-system) event))



(defmulti create (fn [& arg-list] (first arg-list)))
(defmethod create :post ([& arg-list] ))
(defmethod create :asset ([& arg-list] ))
(defmethod create :tag ([& arg-list] ))

(defmulti retrieve (fn [& arg-list] (first arg-list)))
(defmethod retrieve :post ([& arg-list] ))
(defmethod retrieve :asset ([& arg-list] ))
(defmethod retrieve :tag ([& arg-list] ))

(defmulti update (fn [& arg-list] (first arg-list)))
(defmethod update :post ([& arg-list] ))
(defmethod update :asset ([& arg-list] ))
(defmethod update :tag ([& arg-list] ))

(defmulti delete (fn [& arg-list] (first arg-list)))
(defmethod delete :post ([& arg-list] ))
(defmethod delete :asset ([& arg-list] ))
(defmethod delete :tag ([& arg-list] ))

(defmulti find (fn [& arg-list] (first arg-list)))
(defmethod find :post ([& arg-list] ))
(defmethod find :asset ([& arg-list] ))
(defmethod find :tag ([& arg-list] ))

(defmulti list (fn [& arg-list] (first arg-list)))
(defmethod list :post ([& arg-list] ))
(defmethod list :asset ([& arg-list] ))
(defmethod list :tag ([& arg-list] ))


;; Posts
(defn create-post [title content content-type created-date modified-date] )
(defn retrieve-post [ID] )
(defn update-post [ID update-map] )
(defn delete-post [ID] )
(defn find-posts [param-map] )
(defn list-posts [] )


;; Assets
(defn create-asset [name type asset] )
(defn retrieve-asset [ID] )
(defn update-asset [ID update-map] )
(defn delete-asset [ID] )
(defn find-assets [param-map] )
(defn list-assets [] )


;; Tags
(defn create-tag [name] )
(defn retrieve-tag [ID] )
(defn update-tag [ID update-map] )
(defn delete-tag [ID] )
(defn find-tags [param-map] )
(defn list-tags [] )
