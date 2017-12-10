(ns hikari-cp.core
  (:import com.zaxxer.hikari.HikariConfig
           com.zaxxer.hikari.HikariDataSource
           javax.sql.DataSource
           clojure.lang.ExceptionInfo)
  (:require [org.tobereplaced.lettercase :refer [mixed-name]]
            [schema.core :as s]))

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
   "mariadb"        "org.mariadb.jdbc.MySQLDataSource"
   "mysql"          "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"
   "sqlserver-jtds" "net.sourceforge.jtds.jdbcx.JtdsDataSource"
   "sqlserver"      "com.microsoft.sqlserver.jdbc.SQLServerDataSource"
   "oracle"         "oracle.jdbc.pool.OracleDataSource"
   "pgjdbc-ng"      "com.impossibl.postgres.jdbc.PGDataSource"
   "postgresql"     "org.postgresql.ds.PGSimpleDataSource"
   "fdbsql"         "com.foundationdb.sql.jdbc.ds.FDBSimpleDataSource"
   "sybase"         "com.sybase.jdbc4.jdbc.SybDataSource"
   "sqlite"         "org.sqlite.JDBC"})

(def ^{:private true} AdaptersList
  (apply s/enum (keys adapters-to-datasource-class-names)))

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

(def ^{:private true} IntGte0
  (s/both s/Int (s/pred gte-0? 'gte-0?)))

(def ^{:private true} IntGte1
  (s/both s/Int (s/pred gte-1? 'gte-1?)))

(def ^{:private true} IntGte1000
  (s/both s/Int (s/pred gte-1000? 'gte-1000?)))

(def ^{:private true} IntGte2000
  (s/both s/Int (s/pred leak-threshold? 'leak-threshold?)))

(def BaseConfigurationOptions
  {:auto-commit        s/Bool
   :read-only          s/Bool
   :connection-timeout IntGte1000
   :validation-timeout IntGte1000
   :idle-timeout       IntGte0
   :max-lifetime       IntGte0
   :minimum-idle       IntGte0
   :maximum-pool-size  IntGte1
   (s/optional-key :leak-detection-threshold) IntGte2000
   :register-mbeans    s/Bool
   (s/optional-key :connection-init-sql) s/Str
   (s/optional-key :metric-registry) s/Any
   (s/optional-key :health-check-registry) s/Any
   s/Keyword           s/Any})

(def AdapterConfigurationOptions
  (assoc BaseConfigurationOptions
         :adapter AdaptersList))

(def JDBCUrlConfigurationOptions
  (assoc BaseConfigurationOptions
         :jdbc-url s/Str
         (s/optional-key :driver-class-name) s/Str))

(def DatasourceConfigurationOptions
  (assoc BaseConfigurationOptions
         :datasource DataSource))

(def DatasourceClassnameConfigurationOptions
  (assoc BaseConfigurationOptions
         :datasource-class-name s/Str))

;(s/optional-key :driver-class-name)
(def ConfigurationOptions (s/conditional
                             :datasource DatasourceConfigurationOptions
                             :datasource-class-name DatasourceClassnameConfigurationOptions
                             :adapter AdapterConfigurationOptions
                             :jdbc-url JDBCUrlConfigurationOptions
                             :else AdapterConfigurationOptions))


(defn- exception-message
  ""
  ^String [^ExceptionInfo e]
  (format "Invalid configuration options: %s" (keys (:error (.getData e)))))

(defmulti translate-property keyword)
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
  (try
    (s/validate ConfigurationOptions (merge default-datasource-options options))
    (catch ExceptionInfo e
      (throw
       (IllegalArgumentException. (exception-message e))))))

(defn datasource-config
  "Create datasource config from `datasource-options`"
  [datasource-options]
  (let [config (HikariConfig.)
        options               (validate-options datasource-options)
        not-core-options      (apply dissoc options
                                     :username :password :pool-name :connection-test-query
                                     :configure :leak-detection-threshold :adapter :jdbc-url
                                     :datasource-class-name :driver-class-name :connection-init-sql
                                     :metric-registry :health-check-registry
                                     (keys BaseConfigurationOptions))
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
                metric-registry
                health-check-registry]} options]
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
    (when leak-detection-threshold
      (.setLeakDetectionThreshold config ^Long leak-detection-threshold))
    (when configure
      (configure config))
    (when connection-init-sql
      (.setConnectionInitSql config connection-init-sql))
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
