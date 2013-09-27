(ns stefon.shell.kernel

  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.core.async :as async :refer :all]
            [schema.core :as s]

            [stefon.schema :as ss]
            [stefon.domain :as domain]
            [stefon.shell.plugin :as plugin]
            [stefon.shell.functions :as functions]))


(ss/turn-on-validation)


;; SYSTEM structure & functions
(def ^{:doc "In memory representation of the running system structures"} *SYSTEM* (atom nil))

(defn generate-system []
  {:system nil
   :channel-list []

   :send-fns []
   :recieve-fns []})


(s/defn add-to-channel-list [new-channel :- { (s/required-key :id) s/String
                                              (s/required-key :channel) s/Any}]
  (swap! *SYSTEM* (fn [inp]
                    (update-in inp [:channel-list] (fn [ii] (into [] (conj ii new-channel)))))))


;; CREATE Channels
(defn generate-channel
  ([] (generate-channel (str (java.util.UUID/randomUUID))))
  ([channelID]
     {:id channelID
      :channel (chan)}))

(defn generate-kernel-channel []
  (generate-channel "kernel-channel"))



;; GET a Channel
(defn get-channel [ID]
  (->> (:channel-list @*SYSTEM*) (filter #(= ID (:id %))) first))

(defn get-kernel-channel []
  (get-channel "kernel-channel"))



;; LOAD Config information
(defn load-config-raw []
  (load-string (slurp (io/resource "config.edn"))))

(def load-config (memoize load-config-raw))


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


(defn kernel-handler [message]
  (println (str ">> kernel-handler CALLED > " message)))

(defn start-system
  ([] (start-system @*SYSTEM* kernel-handler))
  ([system khandler]

     ;; CREATE System
     (swap! *SYSTEM* (fn [inp] (generate-system)))

     ;; Kernel CHANNEL
     (add-to-channel-list (generate-kernel-channel))

     ;; Setup the system atom & attach plugin channels
     #_(swap! *SYSTEM* (fn [inp]

                       #_(let [with-plugin-system (plugin/create-plugin-system system)]
                           (attach-kernel with-plugin-system khandler)
                           with-plugin-system)))))



;; Posts
#_(defn create-post [title content content-type created-date modified-date assets tags]
  (functions/create *SYSTEM* :posts 'stefon.domain.Post title content content-type created-date modified-date assets tags))
#_(defn retrieve-post [ID] (functions/retrieve *SYSTEM* :posts ID))
#_(defn update-post [ID update-map] (functions/update *SYSTEM* :posts ID update-map))
#_(defn delete-post [ID] (functions/delete *SYSTEM* :posts ID))
#_(defn find-posts [param-map] (functions/find *SYSTEM* :posts param-map))
#_(defn list-posts [] (functions/list *SYSTEM* :posts))


;; Assets
#_(defn create-asset [name type asset] (functions/create *SYSTEM* :assets 'stefon.domain.Asset name type asset))
#_(defn retrieve-asset [ID] (functions/retrieve *SYSTEM* :assets ID))
#_(defn update-asset [ID update-map] (functions/update *SYSTEM* :assets ID update-map))
#_(defn delete-asset [ID] (functions/delete *SYSTEM* :assets ID))
#_(defn find-assets [param-map] (functions/find *SYSTEM* :assets param-map))
#_(defn list-assets [] (functions/list *SYSTEM* :assets))


;; Tags
#_(defn create-tag [name] (functions/create *SYSTEM* :tags 'stefon.domain.Tag name))
#_(defn retrieve-tag [ID] (functions/retrieve *SYSTEM* :tags ID))
#_(defn update-tag [ID update-map] (functions/update *SYSTEM* :tags ID update-map))
#_(defn delete-tag [ID] (functions/delete *SYSTEM* :tags ID))
#_(defn find-tags [param-map] (functions/find *SYSTEM* :tags param-map))
#_(defn list-tags [] (functions/list *SYSTEM* :tags))
