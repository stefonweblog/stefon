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



;; CRUD Wrappers around kernel functions
(defmulti create (fn [& arg-list] (first arg-list)))
(defmethod create :post ([& arg-list]
                           (let [args (rest arg-list)]
                             (kernel/create-post (nth args 0)
                                                 (nth args 1)
                                                 (nth args 2)
                                                 (nth args 3)
                                                 (nth args 4)))))
(defmethod create :asset ([& arg-list]
                            (let [args (rest arg-list)]
                              (kernel/create-asset (nth args 0)
                                                   (nth args 1)
                                                   (nth args 2)))))
(defmethod create :tag ([& arg-list] (let [args (rest arg-list)] (kernel/create-tag (first args) ))))


(defmulti retrieve (fn [& arg-list] (first arg-list)))
(defmethod retrieve :post ([& arg-list] (let [args (rest arg-list)] (kernel/retrieve-post (first args) ))))
(defmethod retrieve :asset ([& arg-list] (let [args (rest arg-list)] (kernel/retrieve-asset (first args)))))
(defmethod retrieve :tag ([& arg-list] (let [args (rest arg-list)] (kernel/retrieve-tag (first args) ))))


(defmulti update (fn [& arg-list] (first arg-list)))
(defmethod update :post ([& arg-list] (let [args (rest arg-list)] (kernel/update-post (first args) (second args)))))
(defmethod update :asset ([& arg-list] (let [args (rest arg-list)] (kernel/update-asset (first args) (second args)))))
(defmethod update :tag ([& arg-list] (let [args (rest arg-list)] (kernel/update-tag (first args) (second args)))))


(defmulti delete (fn [& arg-list] (first arg-list)))
(defmethod delete :post ([& arg-list] (let [args (rest arg-list)] (kernel/delete-post (first args) ))))
(defmethod delete :asset ([& arg-list] (let [args (rest arg-list)] (kernel/delete-asset (first args) ))))
(defmethod delete :tag ([& arg-list] (let [args (rest arg-list)] (kernel/delete-tag (first args) ))))


(defmulti find (fn [& arg-list] (first arg-list)))
(defmethod find :post ([& arg-list] (let [args (rest arg-list)] (kernel/find-posts (first args)))))
(defmethod find :asset ([& arg-list] (let [args (rest arg-list)] (kernel/find-assets (first args) ))))
(defmethod find :tag ([& arg-list] (let [args (rest arg-list)] (kernel/find-tags (first args) ))))


(defmulti list (fn [& arg-list] (first arg-list)))
(defmethod list :post ([& arg-list] (let [args (rest arg-list)] (kernel/list-posts))))
(defmethod list :asset ([& arg-list] (let [args (rest arg-list)] (kernel/list-assets))))
(defmethod list :tag ([& arg-list] (let [args (rest arg-list)] (kernel/list-tags))))
