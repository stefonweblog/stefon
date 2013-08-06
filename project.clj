(defproject stefon "0.1.0-SNAPSHOT"
  :description "A composable blogging engine in Clojure."
  :url "https://github.com/twashing/stefon"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cljs-uuid "0.0.4"]
                 [lamina "0.5.0-rc3"]
                 [com.datomic/datomic "0.8.3335"
                  :exclusions [org.slf4j/slf4j-nop org.slf4j/log4j-over-slf4j]]

                 ;; Plugins
                 [stefon-datomic "0.1.0-SNAPSHOT"]]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [midje "1.5.0"]]}}

  :resource-paths ["resources/"]
  )
