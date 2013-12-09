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

  ([]
     (let [component (stefon.core.Component. nil)]
       (start-system (.init component {}))))
  ([system-state]
     (alter-var-root #'*SYSTEM* (fn [inp] system-state))))

(defn get-system []
  *SYSTEM*)
