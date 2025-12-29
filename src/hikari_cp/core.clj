(ns hikari-cp.core
  (:import com.zaxxer.hikari.HikariConfig
           com.zaxxer.hikari.HikariDataSource
           javax.sql.DataSource)
  (:require [org.tobereplaced.lettercase :refer [mixed-name]]
            [clojure.spec.alpha :as s]))

(def default-datasource-options
  {:auto-commit        true
   :read-only          false
   :connection-timeout 30000
   :validation-timeout 5000
   :idle-timeout       600000
   :max-lifetime       1800000
   :minimum-idle       10
   :maximum-pool-size  10
   :register-mbeans    false})

(def ^{:private true} adapters-to-datasource-class-names
  {"derby"          "org.apache.derby.jdbc.ClientDataSource"
   "firebird"       "org.firebirdsql.pool.FBSimpleDataSource"
   "db2"            "com.ibm.db2.jcc.DB2SimpleDataSource"
   "h2"             "org.h2.jdbcx.JdbcDataSource"
   "hsqldb"         "org.hsqldb.jdbc.JDBCDataSource"
   "mariadb"        "org.mariadb.jdbc.MariaDbDataSource"
   "mysql"          "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"
   "mysql8"         "com.mysql.cj.jdbc.MysqlDataSource"
   "neo4j"          "org.neo4j.jdbc.DataSource"
   "sqlserver-jtds" "net.sourceforge.jtds.jdbcx.JtdsDataSource"
   "sqlserver"      "com.microsoft.sqlserver.jdbc.SQLServerDataSource"
   "oracle"         "oracle.jdbc.pool.OracleDataSource"
   "pgjdbc-ng"      "com.impossibl.postgres.jdbc.PGDataSource"
   "postgresql"     "org.postgresql.ds.PGSimpleDataSource"
   "fdbsql"         "com.foundationdb.sql.jdbc.ds.FDBSimpleDataSource"
   "sybase"         "com.sybase.jdbc4.jdbc.SybDataSource"
   "sqlite"         "org.sqlite.SQLiteDataSource"})

(defn- gte-0?
  "Returns true if num is greater than or equal 0, else false"
  [x]
  (>= x 0))

(defn- gte-1?
  "Returns true if num is greater than or equal 1, else false"
  [x]
  (>= x 1))

(defn- gte-1000?
  "Returns true if num is greater than or equal 1000, else false"
  [x]
  (>= x 1000))

(defn- leak-threshold?
  "Returns true only if x is acceptable value, 0 or greater-than-equal 2000"
  [x]
  (or (== 0 x) (>= x 2000)))

(s/def ::auto-commit
  boolean?)

(s/def ::read-only
  boolean?)

(s/def ::register-mbeans
  boolean?)

(s/def ::connection-timeout
  (s/and integer? gte-1000?))

(s/def ::validation-timeout
  (s/and integer? gte-1000?))

(s/def ::idle-timeout
  (s/and integer? gte-0?))

(s/def ::max-lifetime
  (s/and integer? gte-0?))

(s/def ::minimum-idle
  (s/and integer? gte-0?))

(s/def ::maximum-pool-size
  (s/and integer? gte-1?))

(s/def ::leak-detection-threshold
  (s/and integer? leak-threshold?))

(s/def ::connection-init-sql
  string?)

(s/def ::transaction-isolation
  string?)

(s/def ::basic-options
  (s/and (s/keys :req-un [::auto-commit
                          ::connection-timeout
                          ::idle-timeout
                          ::max-lifetime
                          ::maximum-pool-size
                          ::minimum-idle
                          ::read-only
                          ::register-mbeans
                          ::validation-timeout]
                 :opt-un [::connection-timeout
                          ::transaction-isolation
                          ::leak-detection-threshold])
         ;; Make sure that if the user provides the class
         ;; name using the deprecated keyword we'll throw an
         ;; exception instead of silently failing.
         #(not (contains? % :datasource-classname))))

(s/def ::datasource
  #(instance? DataSource %))

(s/def ::datasource-class-name
  string?)

(s/def ::datasource-options
  (s/keys :req-un [::datasource]))

(s/def ::datasource-class-name-options
  (s/keys :req-un [::datasource-class-name]))

(s/def ::adapter
  (set (keys adapters-to-datasource-class-names)))

(s/def ::adapter-options
  (s/keys :req-un [::adapter]))

(s/def ::jdbc-url
  string?)

(defn- valid-driver-class?
  "Returns true if the class name is a valid JDBC Driver class.
   Checks that:
   1. The class exists on the classpath
   2. The class implements java.sql.Driver interface"
  [class-name]
  (try
    (let [clazz (Class/forName class-name)]
      (.isAssignableFrom java.sql.Driver clazz))
    (catch ClassNotFoundException _
      false)
    (catch Exception _
      false)))

(s/def ::driver-class-name
  (s/and string? valid-driver-class?))

(s/def ::jdbc-url-options
  (s/keys :req-un [::jdbc-url]
          :opt-un [::driver-class-name]))

(s/def ::configuration-options
  (s/and ::basic-options
         (s/or :datasource ::datasource-options
               :datasource-class-name ::datasource-class-name-options
               :adapter ::adapter-options
               :jdbc-url ::jdbc-url-options)))

(defmulti translate-property keyword)
(defmethod translate-property :tinyInt1isBit [_] "tinyInt1isBit")
(defmethod translate-property :tiny-int1is-bit [_] "tinyInt1isBit")
(defmethod translate-property :use-ssl [_] "useSSL")
(defmethod translate-property :useSSL [_] "useSSL")
(defmethod translate-property :default [x] (mixed-name x))

(defn- add-datasource-property
  ""
  [^HikariConfig config property value]
  (when-not (nil? value)
    (.addDataSourceProperty config (translate-property property) value)))

(defn validate-options
  "Validate `options`"
  [options]
  (let [merged (merge default-datasource-options options)]
    (if (s/valid? ::configuration-options merged)
      merged
      (throw (IllegalArgumentException.
               ^String (s/explain-str ::configuration-options merged))))))

(def ^:private core-options
  [:adapter
   :auto-commit
   :configure
   :connection-init-sql
   :connection-test-query
   :connection-timeout
   :datasource-class-name
   :driver-class-name
   :health-check-registry
   :idle-timeout
   :jdbc-url
   :leak-detection-threshold
   :max-lifetime
   :maximum-pool-size
   :metric-registry
   :metrics-tracker-factory
   :minimum-idle
   :password
   :pool-name
   :read-only
   :register-mbeans
   :username
   :transaction-isolation
   :validation-timeout])

(defn datasource-config
  "Create datasource config from `datasource-options`"
  [datasource-options]
  (let [config (HikariConfig.)
        options               (validate-options datasource-options)
        not-core-options      (apply dissoc options core-options)
        {:keys [adapter
                datasource
                datasource-class-name
                auto-commit
                configure
                connection-test-query
                connection-timeout
                validation-timeout
                idle-timeout
                max-lifetime
                maximum-pool-size
                minimum-idle
                password
                pool-name
                read-only
                username
                leak-detection-threshold
                register-mbeans
                jdbc-url
                driver-class-name
                connection-init-sql
                transaction-isolation
                metric-registry
                health-check-registry
                metrics-tracker-factory]} options]
    ;; Set pool-specific properties
    (doto config
      (.setAutoCommit          auto-commit)
      (.setReadOnly            read-only)
      (.setConnectionTimeout   connection-timeout)
      (.setValidationTimeout   validation-timeout)
      (.setIdleTimeout         idle-timeout)
      (.setMaxLifetime         max-lifetime)
      (.setMinimumIdle         minimum-idle)
      (.setMaximumPoolSize     maximum-pool-size))
    (when datasource (.setDataSource config datasource))
    (if adapter
      (->> (get adapters-to-datasource-class-names adapter)
           (.setDataSourceClassName config))
      (.setJdbcUrl config jdbc-url))
    (when datasource-class-name (.setDataSourceClassName config datasource-class-name))
    ;; Set optional properties
    (when driver-class-name (.setDriverClassName config driver-class-name))
    (when username (.setUsername config username))
    (when password (.setPassword config password))
    (when pool-name (.setPoolName config pool-name))
    (when connection-test-query (.setConnectionTestQuery config connection-test-query))
    (when metric-registry (.setMetricRegistry config metric-registry))
    (when health-check-registry (.setHealthCheckRegistry config health-check-registry))
    (when metrics-tracker-factory (.setMetricsTrackerFactory config metrics-tracker-factory))
    (when leak-detection-threshold
      (.setLeakDetectionThreshold config ^Long leak-detection-threshold))
    (when configure
      (configure config))
    (when connection-init-sql
      (.setConnectionInitSql config connection-init-sql))
    (when transaction-isolation (.setTransactionIsolation config transaction-isolation))
    (when register-mbeans
      (.setRegisterMbeans config register-mbeans))
    ;; Set datasource-specific properties
    (doseq [[k v] not-core-options]
      (add-datasource-property config k v))
    config))

(defn make-datasource
  "Make datasource from `datasource-options`"
  [datasource-options]
  (let [config (datasource-config datasource-options)
        datasource (HikariDataSource. config)]
    datasource))

(defn close-datasource
  "Close given `datasource`"
  [^HikariDataSource datasource]
  (.close datasource))

(defn is-running?
  "Check if given `datasource` is running"
  [^HikariDataSource datasource]
  (.isRunning datasource))

(defn is-closed?
  "Check if given `datasource` is closed"
  [^HikariDataSource datasource]
  (.isClosed datasource))
