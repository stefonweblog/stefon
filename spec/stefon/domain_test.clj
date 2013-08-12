(ns stefon.domain-test

  (:require [speclj.core :refer :all]
            [stefon.domain :as domain]
            [cljs-uuid.core :as uuid]))

(describe "one"

          (it "Generating a Post type"

              (let [post-type (domain/gen-post-type)
                    uuidS (str (uuid/make-random))]

                (should-not-be-nil post-type)
                (should= {:id uuidS :title "ta" :content "ca" :created-date "07222013"} (into {} (stefon.domain.Post. uuidS "ta" "ca" "07222013")))))

          (it "Generating an Asset type"

              (let [asset-type (domain/gen-asset-type)
                    uuidS (str (uuid/make-random))]

                (should-not-be-nil asset-type)
                (should= {:id uuidS :asset "binary-content" :type "image"} (into {} (stefon.domain.Asset. uuidS "binary-content" "image")))))


          (it "Generating a Tag type"

              (let [tag-type (domain/gen-tag-type)
                    uuidS (str (uuid/make-random))]

                (should-not-be-nil tag-type)
                (should= {:id uuidS :name "reference"} (into {} (stefon.domain.Tag. uuidS "reference"))))))
