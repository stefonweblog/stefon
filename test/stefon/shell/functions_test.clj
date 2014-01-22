(ns stefon.shell.functions-test
  (:require [clojure.test :refer :all]
            [clojure.pprint :as pprint]
            [stefon.shell :as shell]
            [stefon.shell.kernel :as kernel]
            [stefon.shell.kernel-crud :as kcrud]
            [stefon.domain :as domain]))

(deftest one

  (testing "Create a Post"

    (let [x (shell/stop-system)
          system-atom (shell/start-system)
          r1 (kcrud/create-post system-atom "t" "c" "c/t" "0000" "1111" nil nil)]

      (is (not (nil? r1)))
      (is (= stefon.domain.Post (type r1)))

      (is (= 1 (count (kcrud/get-posts))))
      (is (= stefon.domain.Post (type (first (kcrud/get-posts)))))))

  (testing "Retrieve a Post"

      (let [x (shell/stop-system)
            system-atom (shell/start-system)

            r1 (kcrud/create-post system-atom "t" "captain" "c/t" "0000" "1111" nil nil)

            postID (:id r1)
            r2 (kcrud/retrieve-post postID)]

        (is (not (nil? r2)))
        (is (= stefon.domain.Post (type r2)))
        (is (= "captain" (:content r2)))))

  (testing "Update a Post"

      (let [x (shell/stop-system)
            system-atom (shell/start-system)

            r1 (kcrud/create-post system-atom "t1" "fubar" "c/t" "0000" "1111" nil nil)
            postID (:id r1)

            r2 (kcrud/update-post postID {:content "fubar-two"})
            r3 (kcrud/retrieve-post postID)]

        (is (= "fubar-two" (:content r3)))))

  (testing "Delete a Post"

      ;; inserting and checking
      (let [x (shell/stop-system)
            system-atom (shell/start-system)

            r1 (kcrud/create-post system-atom "t" "thing" "c/t" "0000" "1111" nil nil)

            postID (:id r1)
            r2 (kcrud/retrieve-post postID)]

        (is (not (nil? r2)))
        (is (= stefon.domain.Post (type r2)))
        (is (= "thing" (:content r2)))

        ;; deleting and checking
        (let [r2 (kcrud/delete-post postID)]
          (is (nil? (kcrud/retrieve-post postID))))))

  (testing "Find Posts"

      (let [x (shell/stop-system)
            system-atom (shell/start-system)

            r1 (kcrud/create-post system-atom "fubar one" "c1" "c/t" "0000" "1111" nil nil)
            r2 (kcrud/create-post system-atom "fubar two" "c2" "c/t" "0000" "1111" nil nil)

            r3 (kcrud/find-posts {:title "fubar one"}) ;; This SHOULD work

            r4 (kcrud/find-posts {:content "Zzz"}) ;; this should NOT work
            r5 (kcrud/find-posts {:content "Zzz" :title "fubar one"})]

        (is (not (nil? r3)))
        (is (nil? r4))
        (is (nil? r5))

        ;; ensuring a proper count and the correct result
        (is (= 1 (count r3)))
        (is (= (-> r3 first :id) (:id r1)))))

  (testing "List all Posts"

      (let [x (shell/stop-system)
            system-atom (shell/start-system)

            r1 (kcrud/create-post system-atom "fubar one" "c1" "c/t" "0000" "1111" nil nil)
            r2 (kcrud/create-post system-atom "fubar two" "c2" "c/t" "0000" "1111" nil nil)

            r3 (kcrud/list-posts)]

        ;; ensuring not nil, and a proper count
        (is (not (nil? r3)))
        (is (= 2 (count r3)))

        (is (not (empty? (filter #(= (:id %) (:id r1)) r3)))))))
