(ns stefon.domain-test

  (:require [speclj.core :refer :all]
            [stefon.domain :as domain]
            [cljs-uuid.core :as uuid]))


#_(describe "one"

          (it "Generating a Post type"

              (let [;;post-type (domain/gen-post-type)
                    uuidS (str (uuid/make-random))]

                ;;(should-not-be-nil post-type)
                (should= {:id uuidS :title "ta" :content "ca" :content-type "c/t" :created-date "07202013" :modified-date "07222013" :assets nil :tags nil}
                         (into {} (stefon.domain.Post. uuidS "ta" "ca" "c/t" "07202013" "07222013" nil nil)))))

          (it "Generating an Asset type"

              (let [;;asset-type (domain/gen-asset-type)
                    uuidS (str (uuid/make-random))]

                ;;(should-not-be-nil asset-type)
                (should= {:id uuidS :name "myimage" :type "image" :asset "binary-content"}
                         (into {} (stefon.domain.Asset. uuidS "myimage" "image" "binary-content")))))


          (it "Generating a Tag type"

              (let [;;tag-type (domain/gen-tag-type)
                    uuidS (str (uuid/make-random))]

                ;;(should-not-be-nil tag-type)
                (should= {:id uuidS :name "reference"} (into {} (stefon.domain.Tag. uuidS "reference"))))))


#_(domain/def-domain-record TestRecord
  id   {:type :string, :cardinality :one}
  tags {:type :ref,    :cardinality :many})

#_(describe "Domain"
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
                                   (domain/schema (->TestRecord nil nil)))))))
