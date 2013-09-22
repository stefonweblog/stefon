(defproject stefon "0.1.0-SNAPSHOT"
  :description "A composable blogging engine in Clojure."
  :url "https://github.com/twashing/stefon"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cljs-uuid "0.0.4"]
                 [prismatic/schema "0.1.3"]
                 [org.clojure/core.async "0.1.222.0-83d0c2-alpha"]
                 [com.datomic/datomic "0.8.3335"
                  :exclusions [org.slf4j/slf4j-nop org.slf4j/log4j-over-slf4j]]

                 ;; Plugins
                 #_[stefon-datomic "0.1.0-SNAPSHOT"]]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [speclj "2.5.0"]]}}

  :plugins [[speclj "2.5.0"]]

  :test-paths ["spec"]

  :resource-paths ["resources/"]
  )
