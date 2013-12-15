(ns stefon.schema
  (:require [schema.utils]))


(defn turn-on-validation
  "Turning on schema validation by default"
  ([] (turn-on-validation true))
  ([bool]
     (.set_cell schema.utils/use-fn-validation bool)))
