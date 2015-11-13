(ns hikari-cp.core
  (:import com.zaxxer.hikari.HikariConfig com.zaxxer.hikari.HikariDataSource)
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
   :maximum-pool-size  10})

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
   "sybase"         "com.sybase.jdbcx.SybDataSource"
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

(def ^{:private true} IntGte0
  (s/both s/Int (s/pred gte-0? 'gte-0?)))

(def ^{:private true} IntGte1
  (s/both s/Int (s/pred gte-1? 'gte-1?)))

(def ^{:private true} IntGte1000
  (s/both s/Int (s/pred gte-1000? 'gte-1000?)))

(def ConfigurationOptions
  {:auto-commit        s/Bool
   :read-only          s/Bool
   :connection-timeout IntGte1000
   :validation-timeout IntGte1000
   :idle-timeout       IntGte0
   :max-lifetime       IntGte0
   :minimum-idle       IntGte0
   :maximum-pool-size  IntGte1
   :adapter            AdaptersList
   s/Keyword           s/Any})

(defn- exception-message
  ""
  [e]
  (format "Invalid configuration options: %s" (keys (:error (.getData e)))))

(defn- add-datasource-property
  ""
  [config property value]
  (if (not (nil? value)) (.addDataSourceProperty config (mixed-name property) value)))

(defn validate-options
  ""
  [options]
  (try
    (s/validate ConfigurationOptions (merge default-datasource-options options))
    (catch clojure.lang.ExceptionInfo e
      (throw
       (IllegalArgumentException. (exception-message e))))))

(defn datasource-config
  ""
  [datasource-options]
  (let [config (HikariConfig.)
        options               (validate-options datasource-options)
        not-core-options      (apply dissoc options
                                     :username :password :pool-name :connection-test-query :configure
                                     (keys ConfigurationOptions))
        {:keys [adapter
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
                username]} options
        datasource-class-name (get
                               adapters-to-datasource-class-names
                               adapter)]
    ;; Set pool-specific properties
    (doto config
      (.setAutoCommit          auto-commit)
      (.setReadOnly            read-only)
      (.setConnectionTimeout   connection-timeout)
      (.setValidationTimeout   validation-timeout)
      (.setIdleTimeout         idle-timeout)
      (.setMaxLifetime         max-lifetime)
      (.setMinimumIdle         minimum-idle)
      (.setMaximumPoolSize     maximum-pool-size)
      (.setDataSourceClassName datasource-class-name))
    ;; Set optional properties
    (if username (.setUsername config username))
    (if password (.setPassword config password))
    (if pool-name (.setPoolName config pool-name))
    (if connection-test-query (.setConnectionTestQuery config connection-test-query))
    (when configure
      (configure config))
    ;; Set datasource-specific properties
    (doseq [[k v] not-core-options]
      (add-datasource-property config k v))
    config))

(defn make-datasource
  ""
  [datasource-options]
  (let [config (datasource-config datasource-options)
        datasource (HikariDataSource. config)]
    datasource))

(defn close-datasource
  ""
  [datasource]
  (.close datasource))
