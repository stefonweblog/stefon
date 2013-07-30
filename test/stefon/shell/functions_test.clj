(ns stefon.shell.functions-test

  (:use [midje.sweet])
  (:require [stefon.shell :as shell]
            [clojure.pprint :as pprint]))



(against-background [(before :facts (shell/start-system))
                     (after :facts (shell/stop-system))]


                    ;; ====
                    (fact "Create a Post"

                          (let [r1 (shell/create-post "t" "c" "0000")]

                            r1 =not=> nil?
                            (type r1) => stefon.domain.Post
                            (count (:posts @shell/*SYSTEM*)) => 1))

                    (fact "Retrieve a Post"

                          (let [r1 (shell/create-post "t" "captain" "0000")

                                postID (:id r1)
                                r2 (shell/retrieve-post postID)]

                            r2 =not=> nil?
                            (type r2) => stefon.domain.Post
                            (:content r2) => "captain"))

                    (fact "Update a Post"

                          (let [r1 (shell/create-post "t1" "fubar" "0000")
                                postID (:id r1)

                                r2 (shell/update-post postID {:content "fubar-two"})
                                r3 (shell/retrieve-post postID)]

                            (:content r3) => "fubar-two"))

                    (fact "Delete a Post"

                          ;; inserting and checking
                          (let [r1 (shell/create-post "t" "thing" "0000")

                                postID (:id r1)
                                r2 (shell/retrieve-post postID)]

                            r2 =not=> nil?
                            (type r2) => stefon.domain.Post
                            (:content r2) => "thing"

                            ;; deleting and checking
                            (let [r2 (shell/delete-post postID)]

                              (shell/retrieve-post postID) => nil?)))

                    (fact "Find Posts"

                          (let [r1 (shell/create-post "fubar one" "c1" "0000")
                                r2 (shell/create-post "fubar two" "c2" "0000")

                                r3 (shell/find-posts {:title "fubar one"}) ;; This SHOULD work

                                r4 (shell/find-posts {:content "Zzz"}) ;; this should NOT work
                                r5 (shell/find-posts {:content "Zzz" :title "fubar one"})
                                ]

                            r3 =not=> nil?

                            r4 => nil?
                            r5 => nil?

                            ;; ensuring a proper count and the correct result
                            (count r3) => 1
                            (-> r3 first :id) => (:id r1)
                            ))

                    (fact "List all Posts"

                          (let [r1 (shell/create-post "fubar one" "c1" "0000")
                                r2 (shell/create-post "fubar two" "c2" "0000")

                                r3 (shell/list-posts)
                                ]

                            ;; ensuring not nil, and a proper count
                            r3 =not=> nil?
                            (count r3) => 2
                            (filter #(= (:id %) (:id r1)) r3) =not=> empty?
                            ))

)
