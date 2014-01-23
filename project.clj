(defproject stefon "0.1.1-SNAPSHOT"
  :description "A composable blogging engine in Clojure."
  :url "https://github.com/twashing/stefon"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cljs-uuid "0.0.4"]
                 [prismatic/schema "0.1.3"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [com.taoensso/timbre "3.0.0-RC4"]

                 ;; Plugins
                 #_[stefon-datomic "0.1.1-SNAPSHOT"]
                 [stefonweblog/stefon-compojure "0.1.0-SNAPSHOT"]]

  :profiles {:dev {:source-paths ["dev" "plugins"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [speclj "2.5.0"]
                                  [midje "1.5.1"]
                                  #_[jig "1.2.0"]]
                   :resource-paths ["dev-resources"]}}

  :plugins [[speclj "2.5.0"]]

  :test-paths ["test" "spec"]

  :resource-paths ["resources/"] )

;; To run tests
;; lein midje

;; To autorun tests
;; lein midje :autotest :config test/etc/midje.clj
