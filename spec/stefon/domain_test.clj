(ns stefon.domain-test

  (:require [speclj.core :refer :all]
            [stefon.domain :as domain]
            [cljs-uuid.core :as uuid]))

(describe "one"

          (it "Generating a Post type"

              (let [post-type (domain/gen-post-type)
                    uuidS (str (uuid/make-random))]


                (println "> Post type[" (type post-type) "] > Post[" post-type "]")

                (should-not-be-nil post-type)
                (should= {:id uuidS :title "ta" :content "ca" :content-type "c/t" :created-date "07202013" :modified-date "07222013"}
                         (into {} (stefon.domain.Post. uuidS "ta" "ca" "c/t" "07202013" "07222013")))))

          #_(it "Generating an Asset type"

              (let [asset-type (domain/gen-asset-type)
                    uuidS (str (uuid/make-random))]

                (should-not-be-nil asset-type)
                (should= {:id uuidS :asset "binary-content" :type "image"} (into {} (stefon.domain.Asset. uuidS "binary-content" "image")))))


          #_(it "Generating a Tag type"

              (let [tag-type (domain/gen-tag-type)
                    uuidS (str (uuid/make-random))]

                (should-not-be-nil tag-type)
                (should= {:id uuidS :name "reference"} (into {} (stefon.domain.Tag. uuidS "reference"))))))15
