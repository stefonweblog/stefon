(ns stefon.domain-test
  (:require [speclj.core :refer :all]
            [stefon.domain :as domain :refer :all]))

;; generate domain Classes

(def-domain-record TestRecord
  id   {:type :string, :cardinality :one}
  tags {:type :ref,    :cardinality :many})

(describe "Domain"
  (describe "Test domain record"
    (let [record (->TestRecord 1 ["t1" "t2"])]

      (it "should have a normal accessors" 
          (should= 1 (:id record))
          (should= ["t1" "t2"] (:tags record)))

      (it "should have a global schema var"
          (should= #{{:name :id, :type :string, :cardinality :one}
                     {:name :tags, :type :ref, :cardinality :many}}
                   testrecord-schema))

      (it "should extend the Domain protocol"
          (should= #{{:name :id, :type :string, :cardinality :one}
                     {:name :tags, :type :ref, :cardinality :many}}
                   (schema (->TestRecord nil nil)))))))
