(ns stefon.shell.kernel)

(defn generate-system []
  {:domain {:posts []
            :assets []
            :tags []}
   :channel-list []

   :send-fns []
   :recieve-fns []

   :tee-fns []})

(def ^:dynamic *SYSTEM* "The system state" nil)


(defn start-system
  "Start the system and state"

  ([]
     (start-system {:stefon/system (generate-system)}))
  ([system-state]
     (alter-var-root #'*SYSTEM* (fn [inp] system-state))))

(defn get-system [] *SYSTEM*)

(defn get-plugin-fn [plugin-ns]
  ('plugin (ns-publics plugin-ns)))

(defn load-plugin [plugin-ns]

  (let [plugin-fn (get-plugin-fn plugin-ns)
        receivefn (plugin-fn)]

    ))
