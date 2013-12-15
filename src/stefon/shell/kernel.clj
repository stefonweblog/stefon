(ns stefon.shell.kernel
  (:require [clojure.core.async :as async :refer :all]
            [stefon.shell.plugin :as plugin]
            [schema.core :as s]))

(defn generate-system []
  {:domain {:posts []
            :assets []
            :tags []}
   :channel-list []

   :send-fns []
   :recieve-fns []

   :tee-fns []})

;; ====
;; System functions
;; ====
(def ^:dynamic *SYSTEM* "The system state" (atom nil))
(defn get-system [] *SYSTEM*)


(defn- add-to-generic [system-atom lookup-key thing]
  (swap! system-atom (fn [inp]
                       (update-in inp [lookup-key] (fn [ii] (into [] (conj ii thing)))))))

(s/defn add-to-channel-list [system-atom new-channel :- { (s/required-key :id) s/String
                                                          (s/required-key :channel) s/Any}]
  (add-to-generic system-atom :channel-list new-channel))

(defn generate-channel
  ([] (generate-channel (str (java.util.UUID/randomUUID))))
  ([channelID]
     {:id channelID
      :channel (chan)}))

(defn generate-kernel-channel []
  (generate-channel "kernel-channel"))


(defn start-system
  "Start the system and state"

  ([]
     (start-system {:stefon/system (generate-system)}))
  ([system-state]

     (swap! *SYSTEM* (fn [inp] system-state))

     (add-to-channel-list (get-system) (generate-kernel-channel))

     (get-system)))


;; ====
;; Incremental steps in the pluging handshakre process
;; ====
(defn get-plugin-fn [plugin-ns]
  ('plugin (ns-publics plugin-ns)))

(defn invoke-plugin-fn [plugin-ns]
  (let [plugin-fn (get-plugin-fn plugin-ns)]
    (plugin-fn)))

(defn attach-plugin [plugin-ns]
  (let [receivefn (invoke-plugin-fn plugin-ns)]
    (plugin/attach-plugin (get-system) receivefn)))

(defn load-plugin [plugin-ns]

)
