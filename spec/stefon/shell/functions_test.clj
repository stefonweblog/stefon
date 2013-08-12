(ns stefon.shell.functions-test

  (:require [speclj.core :refer :all]
            [stefon.shell :as shell]
            [stefon.shell.kernel :as kernel]
            [clojure.pprint :as pprint]))



(describe "one"

          (before (shell/start-system))
          (after (shell/stop-system))


          (it "Create a Post"

                (let [r1 (kernel/create-post "t" "c" "0000")]

                  (should-not-be-nil r1)
                  (should= stefon.domain.Post (type r1))
                  (should= 1 (count (:posts @(kernel/get-system))))))

          (it "Retrieve a Post"

                (let [r1 (kernel/create-post "t" "captain" "0000")

                      postID (:id r1)
                      r2 (kernel/retrieve-post postID)]

                  (should-not-be-nil r2)
                  (should= stefon.domain.Post (type r2))
                  (should= "captain" (:content r2))))

          #_(fact "Update a Post"

                (let [r1 (kernel/create-post "t1" "fubar" "0000")
                      postID (:id r1)

                      r2 (kernel/update-post postID {:content "fubar-two"})
                      r3 (kernel/retrieve-post postID)]

                  (:content r3) => "fubar-two"))

          #_(fact "Delete a Post"

                ;; inserting and checking
                (let [r1 (kernel/create-post "t" "thing" "0000")

                      postID (:id r1)
                      r2 (kernel/retrieve-post postID)]

                  r2 =not=> nil?
                  (type r2) => stefon.domain.Post
                  (:content r2) => "thing"

                  ;; deleting and checking
                  (let [r2 (kernel/delete-post postID)]

                    (kernel/retrieve-post postID) => nil?)))

          #_(fact "Find Posts"

                (let [r1 (kernel/create-post "fubar one" "c1" "0000")
                      r2 (kernel/create-post "fubar two" "c2" "0000")

                      r3 (kernel/find-posts {:title "fubar one"}) ;; This SHOULD work

                      r4 (kernel/find-posts {:content "Zzz"}) ;; this should NOT work
                      r5 (kernel/find-posts {:content "Zzz" :title "fubar one"})
                      ]

                  r3 =not=> nil?

                  r4 => nil?
                  r5 => nil?

                  ;; ensuring a proper count and the correct result
                  (count r3) => 1
                  (-> r3 first :id) => (:id r1)
                  ))

          #_(fact "List all Posts"

                (let [r1 (kernel/create-post "fubar one" "c1" "0000")
                      r2 (kernel/create-post "fubar two" "c2" "0000")

                      r3 (kernel/list-posts)
                      ]

                  ;; ensuring not nil, and a proper count
                  r3 =not=> nil?
                  (count r3) => 2
                  (filter #(= (:id %) (:id r1)) r3) =not=> empty?

                  )))
