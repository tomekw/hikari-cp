(defproject hikari-cp "1.1.1"
  :description "A Clojure wrapper to HikariCP JDBC connection pool"
  :url "https://github.com/tomekw/hikari-cp"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:name "git"
        :url "https://github.com/tomekw/hikari-cp"}
  :dependencies [[org.clojure/clojure         "1.6.0"]
                 [org.tobereplaced/lettercase "1.0.0"]
                 [com.zaxxer/HikariCP         "2.3.2"]
                 [prismatic/schema            "0.3.7"]]
  :profiles {:dev {
                   :dependencies [[expectations               "2.0.16"]
                                  [org.slf4j/slf4j-nop        "1.7.10"]
                                  [org.clojure/java.jdbc      "0.3.6"]
                                  [mysql/mysql-connector-java "5.1.34"]
                                  [org.postgresql/postgresql  "9.3-1102-jdbc41"]]
                   :plugins [[lein-expectations "0.0.7"]]}}
  :aliases {"test" "expectations"})
