(ns stefon.domain
  (:require [clojure.string :as string]))

(defprotocol Domain
  (schema [this] "Gets the schema definition for this domain record"))

(defmacro def-domain-record
  [record-name & args]
  (let [fields (partition 2 args)
        schema-var-sym (symbol (str (string/lower-case record-name) "-schema"))]
    `(do
       (def ~schema-var-sym
         ~(into #{} (for [[field-sym field-props] fields]
                      (assoc field-props :name (keyword field-sym)))))
       (defrecord ~record-name [~@(map first fields)]
         Domain
         (schema [_] ~schema-var-sym)))))

(def-domain-record Post
  id            {:type :string,  :cardinality :one}
  title         {:type :string,  :cardinality :one}
  content       {:type :string,  :cardinality :one}
  content-type  {:type :string,  :cardinality :one}
  created-date  {:type :instant, :cardinality :one}
  modified-date {:type :instant, :cardinality :one}
  assets        {:type :ref,     :cardinality :many}
  tags          {:type :ref,     :cardinality :many})

(def-domain-record Asset
  id            {:type :string, :cardinality :one}
  name          {:type :string, :cardinality :one}
  type          {:type :string, :cardinality :one}
  asset         {:type :string, :cardinality :one})

(def-domain-record Tag
  id            {:type :string, :cardinality :one}
  name          {:type :string, :cardinality :one})
