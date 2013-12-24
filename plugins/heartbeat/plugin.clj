(ns heartbeat.plugin)


(defn receivefn [msg] (println "hearbeat received from kernel: " msg))
(defn plugin
  "Step 1: Simply send back this plugin's handler function"
  []
  receivefn)
(defn plugin-ack
  "We're going to expect an acknowledgement with the following keys:
   '(:id :sendfn :recievefn :channel)"
    [result-map])
