(ns stefon.shell.kernel
  (:require [schema.core :as s]
            [stefon.shell.plugin :as plugin]
            [stefon.shell.kernel-process :as process]))

(defn generate-system []
  {:domain {:posts []
            :assets []
            :tags []}
   :channel-list []

   :send-fns []
   :recieve-fns []

   :tee-fns []})


(def ^:dynamic *SYSTEM* "The system state" (atom nil))
(defn get-system [] *SYSTEM*)


(defn get-channel [ID]
  (plugin/get-channel (get-system) ID))

(defn start-system
  "Start the system and state"

  ([]
     (start-system {:stefon/system (generate-system)}
                   process/kernel-handler))
  ([system-state khandler]

     (swap! (get-system) (fn [inp] system-state))

     (plugin/add-to-channel-list (get-system) (plugin/generate-kernel-channel))

     (plugin/generate-kernel-recieve (get-system) khandler)

     (get-system)))

(defn stop-system []
  (swap! *SYSTEM* (fn [inp] nil)))


;; ====
;; Incremental steps in the pluging handshake process
;; ====
(defn get-plugin-fn [plugin-ns]
  ('plugin (ns-publics plugin-ns)))

(defn invoke-plugin-fn [plugin-ns]
  (let [plugin-fn (get-plugin-fn plugin-ns)]
    (plugin-fn)))

(defn attach-plugin
  ([receivefn]
     (attach-plugin (get-system) receivefn))
  ([system-atom receivefn]
     (plugin/attach-plugin system-atom receivefn)))

(defn attach-plugin-from-ns [plugin-ns]
  (let [receivefn (invoke-plugin-fn plugin-ns)]
    (attach-plugin (get-system) receivefn)))

(defn get-ack-fn [plugin-ns]
  ('plugin-ack (ns-publics plugin-ns)))

(defn attach-plugin-ack [plugin-ns plugin-result]
  (let [ack-fn (get-ack-fn plugin-ns)]
    (ack-fn plugin-result)
    :ack))

(defn load-plugin [plugin-ns]
  (->> plugin-ns
       attach-plugin-from-ns
       (attach-plugin-ack plugin-ns)))
