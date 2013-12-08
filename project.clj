(defproject stefon "0.1.1-SNAPSHOT"
  :description "A composable blogging engine in Clojure."
  :url "https://github.com/twashing/stefon"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cljs-uuid "0.0.4"]
                 [prismatic/schema "0.1.3"]
                 [org.clojure/core.async "0.1.222.0-83d0c2-alpha"]

                 ;; Plugins
                 #_[stefon-datomic "0.1.1-SNAPSHOT"]]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [speclj "2.5.0"]
                                  [midje "1.5.1"]
                                  [jig "1.2.0"]]
                   :resource-paths ["dev-resources"]}}

  :plugins [[speclj "2.5.0"]]

  :test-paths ["test" "spec"]

  :resource-paths ["resources/"] )

;; To run tests
;; lein midje

;; To autorun tests
;; lein midje :autotest :config test/etc/midje.clj
