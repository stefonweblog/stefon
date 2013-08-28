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
;; CREATE
(defmulti create (fn [& arg-list] (first arg-list)))
(defmethod create :post ([& arg-list] (let [args (rest arg-list)]
                                        (kernel/handle-incoming-messages {:stefon.post.create {:parameters {:title (nth args 0)
                                                                                                            :content (nth args 1)
                                                                                                            :content-type (nth args 2)
                                                                                                            :created-date (nth args 3)
                                                                                                            :modified-date (nth args 4)}}}))))
(defmethod create :asset ([& arg-list] (let [args (rest arg-list)]
                                         (kernel/handle-incoming-messages {:stefon.asset.create {:parameters {:name (nth args 0)
                                                                                                              :type (nth args 1)
                                                                                                              :asset (nth args 2)}}}))))

(defmethod create :tag ([& arg-list] (let [args (rest arg-list)]
                                       (kernel/handle-incoming-messages {:stefon.tag.create {:parameters {:name (nth args 0)}}}))))

;; RETRIEVE
(defmulti retrieve (fn [& arg-list] (first arg-list)))
(defmethod retrieve :post ([& arg-list] (let [args (rest arg-list)]
                                          (kernel/handle-incoming-messages {:stefon.post.retrieve {:parameters {:id (nth args 0)}}}))))

(defmethod retrieve :asset ([& arg-list] (let [args (rest arg-list)]
                                           (kernel/handle-incoming-messages {:stefon.asset.retrieve {:parameters {:name (nth args 0)}}}))))

(defmethod retrieve :tag ([& arg-list] (let [args (rest arg-list)]
                                         (kernel/handle-incoming-messages {:stefon.tag.retrieve {:parameters {:name (nth args 0)}}}))))

;; UPDATE
(defmulti update (fn [& arg-list] (first arg-list)))
(defmethod update :post ([& arg-list] (let [args (rest arg-list)]
                                        (kernel/handle-incoming-messages {:stefon.post.update {:parameters {:id (nth args 0)
                                                                                                            :update-map (nth args 1)}}}))))

(defmethod update :asset ([& arg-list] (let [args (rest arg-list)]
                                         (kernel/handle-incoming-messages {:stefon.asset.update {:parameters {:id (nth args 0)
                                                                                                              :update-map (nth args 1)}}}))))

(defmethod update :tag ([& arg-list] (let [args (rest arg-list)]
                                       (kernel/handle-incoming-messages {:stefon.tag.update {:parameters {:id (nth args 0)
                                                                                                          :update-map (nth args 1)}}}))))

;; DELETE
(defmulti delete (fn [& arg-list] (first arg-list)))
(defmethod delete :post ([& arg-list] (let [args (rest arg-list)]
                                        (kernel/handle-incoming-messages {:stefon.post.delete {:parameters {:id (nth args 0)}}}))))

(defmethod delete :asset ([& arg-list] (let [args (rest arg-list)]
                                         (kernel/handle-incoming-messages {:stefon.asset.delete {:parameters {:id (nth args 0)}}}))))

(defmethod delete :tag ([& arg-list] (let [args (rest arg-list)]
                                       (kernel/handle-incoming-messages {:stefon.tag.delete {:parameters {:id (nth args 0)}}}))))

;; FIND
(defmulti find (fn [& arg-list] (first arg-list)))
(defmethod find :post ([& arg-list] (let [args (rest arg-list)]
                                      (kernel/handle-incoming-messages {:stefon.post.find {:parameters {:param-map (nth args 0)}}}))))

(defmethod find :asset ([& arg-list] (let [args (rest arg-list)]
                                       (kernel/handle-incoming-messages {:stefon.asset.find {:parameters {:param-map (nth args 0)}}}))))

(defmethod find :tag ([& arg-list] (let [args (rest arg-list)]
                                     (kernel/handle-incoming-messages {:stefon.tag.find {:parameters {:param-map (nth args 0)}}}))))

;; LIST
(defmulti list (fn [& arg-list] (first arg-list)))
(defmethod list :post ([& arg-list] (let [args (rest arg-list)]
                                      (kernel/handle-incoming-messages {:stefon.post.list {:parameters nil}}))))

(defmethod list :asset ([& arg-list] (let [args (rest arg-list)]
                                       (kernel/handle-incoming-messages {:stefon.asset.list {:parameters nil}}))))

(defmethod list :tag ([& arg-list] (let [args (rest arg-list)]
                                     (kernel/handle-incoming-messages {:stefon.tag.list {:parameters nil}}))))
