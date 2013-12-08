(ns stefon.shell.kernel
  (:require user))

(defn generate-system []
  {:domain {:posts []
            :assets []
            :tags []}
   :channel-list []

   :send-fns []
   :recieve-fns []

   :tee-fns []})

(def *SYSTEM*
  "The system state"
  nil)

(defn start-system
  "Start the system and state"

  ([] (start-system (generate-system)))
  ([system-state]
     (alter-var-root #'*SYSTEM* (fn [inp] system-state))))

(defn get-system []
  *SYSTEM*)
