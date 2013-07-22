(defproject stefon "0.1.0-SNAPSHOT"
  :description "A composable blogging engine in Clojure."
  :url "https://github.com/twashing/stefon"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [midje "1.5.0"]]}}

  )
