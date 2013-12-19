(ns stefon.shell.kernel
  (:require [clojure.core.async :as async :refer :all]
            [schema.core :as s]
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


(defn- add-to-generic [system-atom lookup-key thing]
  (swap! system-atom (fn [inp]
                       (update-in inp [:stefon/system lookup-key] (fn [ii] (into [] (conj ii thing)))))))

(defn get-channel [ID]
  (->> @*SYSTEM* :stefon/system :channel-list (filter #(= ID (:id %))) first))

(defn get-kernel-channel []
  (get-channel "kernel-channel"))

(s/defn add-to-channel-list
  ([new-channel]
     (add-to-channel-list (get-system) new-channel))
  ([system-atom new-channel :- { (s/required-key :id) s/String
                                 (s/required-key :channel) s/Any}]
     (add-to-generic system-atom :channel-list new-channel)))

(s/defn add-to-recievefns [recieve-map :- { (s/required-key :id) s/String
                                            (s/required-key :fn) s/Any}]
  {:pre [(fn? (:fn recieve-map))]}

  (add-to-generic (get-system) :recieve-fns recieve-map))


(defn generate-channel
  ([] (generate-channel (str (java.util.UUID/randomUUID))))
  ([channelID]
     {:id channelID
      :channel (chan)}))

(defn generate-kernel-channel []
  (generate-channel "kernel-channel"))

(defn generate-kernel-recieve [khandler]

  (let [krecieve (plugin/generate-recieve-fn (:channel (get-kernel-channel)))]
     (krecieve (get-system) khandler)
     (add-to-recievefns {:id (:id (get-kernel-channel))
                         :fn krecieve}) ))


(defn start-system
  "Start the system and state"

  ([]
     (start-system {:stefon/system (generate-system)}
                   process/kernel-handler))
  ([system-state khandler]

     (swap! *SYSTEM* (fn [inp] system-state))

     (add-to-channel-list (get-system) (generate-kernel-channel))

     (generate-kernel-recieve khandler)


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

(defn attach-plugin [system-atom receivefn]
  (plugin/attach-plugin system-atom receivefn))

(defn load-plugin [plugin-ns]
  (let [receivefn (invoke-plugin-fn plugin-ns)]
    (attach-plugin (get-system) receivefn)))
