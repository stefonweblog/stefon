(ns user

  (:require [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]))

(defn list-fns-in-ns [input-ns]
  (keys (ns-publics input-ns)))
