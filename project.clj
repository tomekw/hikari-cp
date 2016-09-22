(defproject hikari-cp "1.7.3"
  :description "A Clojure wrapper to HikariCP JDBC connection pool"
  :url "https://github.com/tomekw/hikari-cp"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:name "git"
        :url "https://github.com/tomekw/hikari-cp"}
  :dependencies [[org.clojure/clojure         "1.7.0"]
                 [org.tobereplaced/lettercase "1.0.0"]
                 [com.zaxxer/HikariCP         "2.5.1"]
                 [prismatic/schema            "1.0.4"]]
  :profiles {:dev {
                   :dependencies [[expectations               "2.1.4"]
                                  [org.slf4j/slf4j-nop        "1.7.13"]
                                  [org.clojure/java.jdbc      "0.4.2"]
                                  [mysql/mysql-connector-java "5.1.38"]
                                  [org.postgresql/postgresql  "9.3-1102-jdbc41"]

                                  ; The Oracle driver is only accessible from maven.oracle.com
                                  ; which requires a userId and password
                                  ; https://blogs.oracle.com/dev2dev/entry/how_to_get_oracle_jdbc
                                  ; Or manual download with account
                                  ; http://www.oracle.com/technetwork/database/features/jdbc/default-2280470.html
                                  #_[com.oracle.jdbc/ojdbc7   "12.1.0.2"]]
                   :plugins [[lein-expectations "0.0.7"]]}}
  :aliases {"test" "expectations"})
