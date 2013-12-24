(ns stefon.shell

  (:require [stefon.shell.kernel :as kernel]
            [stefon.shell.kernel-process :as process]
            [stefon.shell.plugin :as plugin]))


(defn create-system []
  (kernel/generate-system))

(defn system-started? []
  (if-not (nil? @(kernel/get-system))
    true
    false))

(defn start-system []
  (kernel/start-system))

(defn stop-system []
  (kernel/stop-system))

(defn attach-plugin [handlerfn]
  (kernel/attach-plugin (kernel/get-system) handlerfn))

(defn attach-plugin-from-ns [plugin-ns]
  (kernel/attach-plugin-from-ns plugin-ns))

(defn load-plugin [plugin-ns]
  (kernel/load-plugin plugin-ns))


;; CRUD Wrappers around kernel functions
;; CREATE
(defmulti create (fn [& arg-list] (first arg-list)))
(defmethod create :post ([& arg-list] (let [args (rest arg-list)]
                                        (process/kernel-handler  {:id "kernel-channel" :message {:stefon.post.create {:parameters {:title (nth args 0)
                                                                                                                                  :content (nth args 1)
                                                                                                                                  :content-type (nth args 2)
                                                                                                                                  :created-date (nth args 3)
                                                                                                                                  :modified-date (nth args 4)
                                                                                                                                  :assets (nth args 5)
                                                                                                                                  :tags (nth args 6)}}}}))))

(defmethod create :asset ([& arg-list] (let [args (rest arg-list)]
                                         (process/kernel-handler {:id "kernel-channel" :message {:stefon.asset.create {:parameters {:name (nth args 0)
                                                                                                                                   :type (nth args 1)
                                                                                                                                   :asset (nth args 2)}}}}))))

(defmethod create :tag ([& arg-list] (let [args (rest arg-list)]
                                       (process/kernel-handler {:id "kernel-channel" :message {:stefon.tag.create {:parameters {:name (nth args 0)}}}}))))

;; RETRIEVE
(defmulti retrieve (fn [& arg-list] (first arg-list)))
(defmethod retrieve :post ([& arg-list] (let [args (rest arg-list)]
                                          (process/kernel-handler {:id "kernel-channel" :message {:stefon.post.retrieve {:parameters {:id (nth args 0)}}}}))))

(defmethod retrieve :asset ([& arg-list] (let [args (rest arg-list)]
                                           (process/kernel-handler {:id "kernel-channel" :message {:stefon.asset.retrieve {:parameters {:name (nth args 0)}}}}))))

(defmethod retrieve :tag ([& arg-list] (let [args (rest arg-list)]
                                         (process/kernel-handler {:id "kernel-channel" :message {:stefon.tag.retrieve {:parameters {:name (nth args 0)}}}}))))

;; UPDATE
(defmulti update (fn [& arg-list] (first arg-list)))
(defmethod update :post ([& arg-list] (let [args (rest arg-list)]
                                        (process/kernel-handler {:id "kernel-channel" :message {:stefon.post.update {:parameters {:id (nth args 0)
                                                                                                                                 :update-map (nth args 1)}}}}))))

(defmethod update :asset ([& arg-list] (let [args (rest arg-list)]
                                         (process/kernel-handler {:id "kernel-channel" :message {:stefon.asset.update {:parameters {:id (nth args 0)
                                                                                                                                   :update-map (nth args 1)}}}}))))

(defmethod update :tag ([& arg-list] (let [args (rest arg-list)]
                                       (process/kernel-handler {:id "kernel-channel" :message {:stefon.tag.update {:parameters {:id (nth args 0)
                                                                                                                               :update-map (nth args 1)}}}}))))

;; DELETE
(defmulti delete (fn [& arg-list] (first arg-list)))
(defmethod delete :post ([& arg-list] (let [args (rest arg-list)]
                                        (process/kernel-handler {:id "kernel-channel" :message {:stefon.post.delete {:parameters {:id (nth args 0)}}}}))))

(defmethod delete :asset ([& arg-list] (let [args (rest arg-list)]
                                         (process/kernel-handler {:id "kernel-channel" :message {:stefon.asset.delete {:parameters {:id (nth args 0)}}}}))))

(defmethod delete :tag ([& arg-list] (let [args (rest arg-list)]
                                       (process/kernel-handler {:id "kernel-channel" :message {:stefon.tag.delete {:parameters {:id (nth args 0)}}}}))))

;; FIND
(defmulti find (fn [& arg-list] (first arg-list)))
(defmethod find :post ([& arg-list] (let [args (rest arg-list)]
                                      (process/kernel-handler {:id "kernel-channel" :message {:stefon.post.find {:parameters {:param-map (nth args 0)}}}}))))

(defmethod find :asset ([& arg-list] (let [args (rest arg-list)]
                                       (process/kernel-handler {:id "kernel-channel" :message {:stefon.asset.find {:parameters {:param-map (nth args 0)}}}}))))

(defmethod find :tag ([& arg-list] (let [args (rest arg-list)]
                                     (process/kernel-handler {:id "kernel-channel" :message {:stefon.tag.find {:parameters {:param-map (nth args 0)}}}}))))

;; LIST
(defmulti list (fn [& arg-list] (first arg-list)))
(defmethod list :post ([& arg-list] (let [args (rest arg-list)]
                                      (process/kernel-handler {:id "kernel-channel" :message {:stefon.post.list {:parameters nil}}}))))

(defmethod list :asset ([& arg-list] (let [args (rest arg-list)]
                                       (process/kernel-handler {:id "kernel-channel" :message {:stefon.asset.list {:parameters nil}}}))))

(defmethod list :tag ([& arg-list] (let [args (rest arg-list)]
                                     (process/kernel-handler {:id "kernel-channel" :message {:stefon.tag.list {:parameters nil}}}))))
