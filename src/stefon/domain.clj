(ns stefon.domain
  (:require [clojure.string :as string]))


(defprotocol Domain
  (schema [this] "Gets the schema definition for this domain object"))

(defn post-schema []

  [{:name :id
    :type :string
    :cardinality :one}
   {:name :title
    :type :string
    :cardinality :one}
   {:name :content
    :type :string
    :cardinality :one}
   {:name :content-type
    :type :string
    :cardinality :one}
   {:name :created-date
    :type :instant
    :cardinality :one}
   {:name :modified-date
    :type :instant
    :cardinality :one}
   {:name :assets
    :type :ref
    :cardinality :many}
   {:name :tags
    :type :ref
    :cardinality :many}
    ])

(defn asset-schema []

  [{:name :id
    :type :string
    :cardinality :one}
   {:name :name
    :type :string
    :cardinality :one}
   {:name :type
    :type :string
    :cardinality :one}
   {:name :asset
    :type :string
    :cardinality :one}])

(defn tag-schema []

  [{:name :id
    :type :string
    :cardinality :one}
   {:name :name
    :type :string
    :cardinality :one}])


;; When using defrecord, it will be generated from the names of the domain's schema definitions
(defn gen-post-type []

  (let [args-vec (into []
                       (map #(-> % :name name symbol)
                            (post-schema))) ]

    ;; ensure that we eval, and create record in the 'stefon.domain' namespace
    (binding [*ns* (find-ns 'stefon.domain)]
      (eval `(defrecord ~(symbol "Post") [~@args-vec])) )))

(defn gen-asset-type []

  (let [args-vec (into []
                       (map #(-> % :name name symbol)
                            (asset-schema))) ]

    (binding [*ns* (find-ns 'stefon.domain)]
      (eval `(defrecord ~(symbol "Asset") [~@args-vec])))))

(defn gen-tag-type []

  (let [args-vec (into []
                       (map #(-> % :name name symbol)
                            (tag-schema))) ]

    (binding [*ns* (find-ns 'stefon.domain)]
      (eval `(defrecord ~(symbol "Tag") [~@args-vec])))))


(gen-post-type)
(gen-asset-type)
(gen-tag-type)

#_(defmacro def-domain-record
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

#_(def-domain-record Post
  id            {:type :string,  :cardinality :one}
  title         {:type :string,  :cardinality :one}
  content       {:type :string,  :cardinality :one}
  content-type  {:type :string,  :cardinality :one}
  created-date  {:type :instant, :cardinality :one}
  modified-date {:type :instant, :cardinality :one}
  assets        {:type :ref,     :cardinality :many}
  tags          {:type :ref,     :cardinality :many})

#_(def-domain-record Asset
  id            {:type :string, :cardinality :one}
  name          {:type :string, :cardinality :one}
  type          {:type :string, :cardinality :one}
  asset         {:type :string, :cardinality :one})

#_(def-domain-record Tag
  id            {:type :string, :cardinality :one}
  name          {:type :string, :cardinality :one})
