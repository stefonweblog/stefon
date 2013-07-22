(ns stefon.domain-test
  (:use midje.sweet)
  (:require [stefon.domain :as domain]))

(fact "Generating a Post type"

      (let [post-type (domain/gen-post-type)]

        post-type =not=> nil?
        (stefon.domain.Post. "ta" "ca" "07222013") => (contains {:title "ta" :content "ca" :created-date "07222013"})))

(fact "Generating an Asset type"

      (let [asset-type (domain/gen-asset-type)]

        asset-type =not=> nil?
        (stefon.domain.Asset. "binary-content" "image") => (contains {:asset "binary-content" :type "image"})))


(fact "Generating a Tag type"

      (let [tag-type (domain/gen-tag-type)]

        tag-type =not=> nil?
        (stefon.domain.Tag. "reference") => (contains {:name "reference"})))
