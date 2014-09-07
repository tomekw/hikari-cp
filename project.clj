(defproject hikari-cp "0.2.0"
  :description "A Clojure wrapper to HikariCP JDBC connection pool"
  :url "https://github.com/tomekw/hikari-cp"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:name "git"
        :url "https://github.com/tomekw/hikari-cp"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.zaxxer/HikariCP "2.0.1"]]
  :profiles {:dev {
                   :dependencies [[org.clojure/java.jdbc      "0.3.5"]
                                  [mysql/mysql-connector-java "5.1.32"]
                                  [org.postgresql/postgresql  "9.3-1102-jdbc41"]]}})
