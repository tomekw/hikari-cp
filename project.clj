(defproject hikari-cp "0.10.0"
  :description "A Clojure wrapper to HikariCP JDBC connection pool"
  :url "https://github.com/tomekw/hikari-cp"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:name "git"
        :url "https://github.com/tomekw/hikari-cp"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [camel-snake-kebab   "0.2.5" :exclusions [org.clojure/clojure]]
                 [com.zaxxer/HikariCP "2.2.5"]
                 [prismatic/schema    "0.3.3"]]
  :profiles {:dev {
                   :dependencies [[expectations              "2.0.13"]
                                  [org.slf4j/slf4j-nop       "1.7.7"]
                                  [org.clojure/java.jdbc     "0.3.6"]
                                  [org.postgresql/postgresql "9.3-1102-jdbc41"]]
                   :plugins [[lein-expectations "0.0.7"]]}}
  :aliases {"test" "expectations"})
