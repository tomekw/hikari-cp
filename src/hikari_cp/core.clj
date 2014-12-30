(ns hikari-cp.core
  (:import com.zaxxer.hikari.HikariConfig com.zaxxer.hikari.HikariDataSource)
  (:require [org.tobereplaced.lettercase :refer [mixed-name]]
            [schema.core :as s]))

(def default-datasource-options
  {:auto-commit        true
   :read-only          false
   :connection-timeout 30000
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
   "sybase"         "com.sybase.jdbcx.SybDataSource"})

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

(defn- gte-100?
  "Returns true if num is greater than or equal 100, else false"
  [x]
  (>= x 100))

(def ^{:private true} IntGte0
  (s/both s/Int (s/pred gte-0? 'gte-0?)))

(def ^{:private true} IntGte1
  (s/both s/Int (s/pred gte-1? 'gte-1?)))

(def ^{:private true} IntGte100
  (s/both s/Int (s/pred gte-100? 'gte-100?)))

(def ConfigurationOptions
  {:auto-commit        s/Bool
   :read-only          s/Bool
   :connection-timeout IntGte100
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
  (if value (.addDataSourceProperty config (mixed-name property) value)))

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
                                     (conj (keys ConfigurationOptions)
                                           :username :password :pool-name :connection-test-query))
        {:keys [adapter
                auto-commit
                connection-test-query
                connection-timeout
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
    ;; Set datasource-specific properties
    (doseq [[k v] not-core-options]
      (add-datasource-property config k v))
    config))

(defn datasource-from-config
  "DEPRECATED

  Function will be removed in version 1.0.0"
  {:deprecated "0.12.0"}
  [config]
  (println "WARNING: datasource-from-config is deprecated and will be removed in version 1.0.0")
  (let [datasource (HikariDataSource. config)] datasource))

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
