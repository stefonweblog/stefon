(ns stefon.shell.kernel

  (:require [clojure.java.io :as io]
            [lamina.core :as lamina]
            [clojure.string :as string]

            [stefon.domain :as domain]
            [stefon.shell.plugin :as plugin]
            [stefon.shell.functions :as functions]))

(declare attach-kernel)


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


;; KERNEL message handling
(defn send-message [event]
  (lamina/enqueue (:channel-spout @*SYSTEM*) event))

(defn handle-incoming-messages
  "Goes through all the keys and passes associated values to system mapped action. Event structures should look like below. Full mappings can be found in resources/config.edn.

   {:stefon.post.create {:parameters {:title \"Latest In Biotechnology\" :content \"Lorem ipsum.\" :created-date \"0000\" }}}"
  [event]

  (let [action-config (:action-mappings (load-config))
        action-keys (keys action-config)

        eventF (:send-event event)
        sendF (:send-handler event)
        filtered-event-keys (keys (select-keys eventF action-keys))]


    ;; ====
    ;; perform actions, based on keys
    (println (str ">> filtered-event-keys[" filtered-event-keys "] / action-config[" action-config "]"))
    (reduce (fn [rslt ekey]

              (let [afn (ekey action-config)
                    params (-> eventF ekey :parameters vals)]

                ;; execute the mapped action
                (println (str ">> execute on key[" ekey "] / payload[" `(~afn ~@params) "]"))
                (let [eval-result (eval `(~afn ~@params) )]

                  ;; send evaluation result back to sender
                  (sendF eval-result))


                ;; notify other plugins what has taken place; replacing :stefon... with :plugin...
                (send-message {(keyword (string/replace (name ekey) #"stefon" "plugin"))
                               {:parameters (-> eventF ekey :parameters)}})))
            []
            filtered-event-keys)

    ;; ====
    ;; pass along any event(s) for which we do not have mappings
    (let [event-less-known-mappings (eval `(~dissoc ~eventF ~@action-keys))]

      (if-not (empty? event-less-known-mappings)

        (do (println (str ">> forwarding unknown events > " event-less-known-mappings))
            (send-message event-less-known-mappings))))))

(defn attach-kernel
  "Attaches a listener / handler to an in coming lamina channel"

  ([system]
     (attach-kernel system handle-incoming-messages))

  ([system message-handler]
     (lamina/receive-all (:channel-spout system) message-handler)))


;; Posts
(defn create-post [title content content-type created-date modified-date assets-ref tags-ref]
  (functions/create *SYSTEM* :posts 'stefon.domain.Post title content content-type created-date modified-date assets-ref tags-ref))
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
