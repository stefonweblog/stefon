(ns stefon.shell.functions-test
  (:require [clojure.test :refer :all]
            [clojure.pprint :as pprint]
            [stefon.shell :as shell]
            [stefon.shell.kernel :as kernel]
            [stefon.shell.kernel-crud :as kcrud]
            [stefon.domain :as domain]))

(deftest one


  (testing "Create a Post"

    (let [xx (shell/stop-system)
          xx (shell/start-system)
          r1 (kcrud/create-post "t" "c" "c/t" "0000" "1111" nil nil)]

      (is (not (nil? r1)))
      (is (= stefon.domain.Post (type r1)))

      (is (= 1 (count (kcrud/get-posts))))
      (is (= stefon.domain.Post (type (first (kcrud/get-posts)))))))

  #_(it "Retrieve a Post"

      (let [r1 (kernel/create-post "t" "captain" "c/t" "0000" "1111" nil nil)

            postID (:id r1)
            r2 (kernel/retrieve-post postID)]

        (should-not-be-nil r2)
        (should= stefon.domain.Post (type r2))
        (should= "captain" (:content r2))))

  #_(it "Update a Post"

      (let [r1 (kernel/create-post "t1" "fubar" "c/t" "0000" "1111" nil nil)
            postID (:id r1)

            r2 (kernel/update-post postID {:content "fubar-two"})
            r3 (kernel/retrieve-post postID)]

        (should= "fubar-two" (:content r3))))

  #_(it "Delete a Post"

      ;; inserting and checking
      (let [r1 (kernel/create-post "t" "thing" "c/t" "0000" "1111" nil nil)

            postID (:id r1)
            r2 (kernel/retrieve-post postID)]

        (should-not-be-nil r2)
        (should= stefon.domain.Post (type r2))
        (should= "thing" (:content r2))

        ;; deleting and checking
        (let [r2 (kernel/delete-post postID)]

          (should-be-nil (kernel/retrieve-post postID)))))

  #_(it "Find Posts"

      (let [r1 (kernel/create-post "fubar one" "c1" "c/t" "0000" "1111" nil nil)
            r2 (kernel/create-post "fubar two" "c2" "c/t" "0000" "1111" nil nil)

            r3 (kernel/find-posts {:title "fubar one"}) ;; This SHOULD work

            r4 (kernel/find-posts {:content "Zzz"}) ;; this should NOT work
            r5 (kernel/find-posts {:content "Zzz" :title "fubar one"})]

        (should-not-be-nil r3)
        (should-be-nil r4)
        (should-be-nil r5)

        ;; ensuring a proper count and the correct result
        (should= 1 (count r3))
        (should= (-> r3 first :id) (:id r1))))

  #_(it "List all Posts"

      (let [r1 (kernel/create-post "fubar one" "c1" "c/t" "0000" "1111" nil nil)
            r2 (kernel/create-post "fubar two" "c2" "c/t" "0000" "1111" nil nil)

            r3 (kernel/list-posts)]

        ;; ensuring not nil, and a proper count
        (should-not-be-nil r3)
        (should= 2 (count r3))

        (should-not (empty? (filter #(= (:id %) (:id r1)) r3))))))
