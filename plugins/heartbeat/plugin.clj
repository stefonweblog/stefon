(ns heartbeat.plugin)


(defn receivefn [msg] (println "hearbeat received from kernel: " msg))
(defn plugin [] receivefn)
(defn plugin-ack [result-map])
