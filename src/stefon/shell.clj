(ns stefon.shell)


(def ^:dynamic *SYSTEM* (atom nil))

(defn create-system []

  {:posts []
   :assets []
   :tags []})

(defn start-system

  ([]
     (start-system (create-system)))

  ([system]

     (swap! *SYSTEM* (fn [inp] system))
     (in-ns 'stefon.shell)))

(defn stop-system []

  (swap! *SYSTEM* (fn [inp]  nil))
  (in-ns 'user))
