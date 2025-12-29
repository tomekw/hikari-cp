(defproject hikari-cp "3.3.0"
  :description "A Clojure wrapper to HikariCP JDBC connection pool"
  :url "https://github.com/tomekw/hikari-cp"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:name "git"
        :url  "https://github.com/tomekw/hikari-cp"}
  :dependencies [[org.clojure/clojure "1.12.4" :scope "provided"]
                 [org.tobereplaced/lettercase "1.0.0"]
                 [com.zaxxer/HikariCP "7.0.2"]]
  :deploy-repositories [["clojars" {:sign-releases false :url "https://clojars.org/repo"}]]
  :profiles {:dev {:dependencies [[org.slf4j/slf4j-nop "2.0.17"]
                                  [org.clojure/java.jdbc "0.7.12"]
                                  [com.h2database/h2 "2.4.240"]
                                  [mysql/mysql-connector-java "8.0.33"]
                                  [org.neo4j.driver/neo4j-java-driver "6.0.2"]
                                  [org.postgresql/postgresql "42.7.8"]
                                  [io.dropwizard.metrics/metrics-core "4.2.37"]
                                  [io.dropwizard.metrics/metrics-healthchecks "4.2.37"]
                                  [io.prometheus/simpleclient "0.16.0"]

                                  ; The Oracle driver is only accessible from maven.oracle.com
                                  ; which requires a userId and password
                                  ; https://blogs.oracle.com/dev2dev/entry/how_to_get_oracle_jdbc
                                  ; Or manual download with account
                                  ; http://www.oracle.com/technetwork/database/features/jdbc/default-2280471.html
                                  #_[com.oracle.jdbc/ojdbc7 "12.1.0.2"]]
                   :plugins      [[lein-ancient "0.7.0"]
                                  [jonase/eastwood "1.4.3"]]}})
