(defproject hikari-cp "2.12.0"
  :description "A Clojure wrapper to HikariCP JDBC connection pool"
  :url "https://github.com/tomekw/hikari-cp"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:name "git"
        :url  "https://github.com/tomekw/hikari-cp"}
  :dependencies [[org.clojure/clojure "1.9.0" :scope "provided"]
                 [org.tobereplaced/lettercase "1.0.0"]
                 [com.zaxxer/HikariCP "3.4.3"]]
  :deploy-repositories [["clojars" {:sign-releases false :url "https://clojars.org/repo"}]]
  :profiles {:dev {:dependencies [[expectations "2.1.10"]
                                  [org.slf4j/slf4j-nop "1.7.30"]
                                  [org.clojure/java.jdbc "0.7.11"]
                                  [mysql/mysql-connector-java "8.0.20"]
                                  [org.neo4j/neo4j-jdbc-driver "4.0.0"]
                                  [org.postgresql/postgresql "42.2.12"]
                                  [io.dropwizard.metrics/metrics-core "4.1.6"]
                                  [io.dropwizard.metrics/metrics-healthchecks "4.1.6"]
                                  [io.prometheus/simpleclient "0.8.1"]

                                  ; The Oracle driver is only accessible from maven.oracle.com
                                  ; which requires a userId and password
                                  ; https://blogs.oracle.com/dev2dev/entry/how_to_get_oracle_jdbc
                                  ; Or manual download with account
                                  ; http://www.oracle.com/technetwork/database/features/jdbc/default-2280471.html
                                  #_[com.oracle.jdbc/ojdbc7 "12.1.0.2"]]
                   :plugins      [[lein-ancient "0.6.15"]
                                  [lein-expectations "0.0.8"]]}}
  :aliases {"test" "expectations"})
