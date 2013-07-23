(ns stefon.domain-test
  (:use midje.sweet)
  (:require [stefon.domain :as domain]
            [cljs-uuid.core :as uuid]))

(fact "Generating a Post type"

      (let [post-type (domain/gen-post-type)
            uuidS (str (uuid/make-random))]

        post-type =not=> nil?
        (stefon.domain.Post. uuidS "ta" "ca" "07222013") => (contains {:id uuidS :title "ta" :content "ca" :created-date "07222013"})))

(fact "Generating an Asset type"

      (let [asset-type (domain/gen-asset-type)
            uuidS (str (uuid/make-random))]

        asset-type =not=> nil?
        (stefon.domain.Asset. uuidS "binary-content" "image") => (contains {:id uuidS :asset "binary-content" :type "image"})))


(fact "Generating a Tag type"

      (let [tag-type (domain/gen-tag-type)
            uuidS (str (uuid/make-random))]

        tag-type =not=> nil?
        (stefon.domain.Tag. uuidS "reference") => (contains {:id uuidS :name "reference"})))
