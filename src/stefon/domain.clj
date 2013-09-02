(ns stefon.domain)


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
    :cardinality :one}])

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
      (eval `(defrecord ~(symbol "Post") [~@args-vec])))))

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
