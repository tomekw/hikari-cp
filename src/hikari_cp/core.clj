(ns hikari-cp.core
  (:import com.zaxxer.hikari.HikariConfig com.zaxxer.hikari.HikariDataSource))

(def ^{:private true} default-datasource-options
  {:auto-commit        true
   :read-only          false
   :connection-timeout 30000
   :idle-timeout       600000
   :max-lifetime       1800000
   :minimum-idle       10
   :maximum-pool-size  10})

(def ^{:private true} adapters-to-datasource-class-names
  {:derby      "org.apache.derby.jdbc.ClientDataSource"
   :firebird   "org.firebirdsql.pool.FBSimpleDataSource"
   :db2        "com.ibm.db2.jcc.DB2SimpleDataSource"
   :h2         "org.h2.jdbcx.JdbcDataSource"
   :hsqldb     "org.hsqldb.jdbc.JDBCDataSource"
   :mariadb    "org.mariadb.jdbc.MySQLDataSource"
   :mysql      "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"
   :sqlserver  "com.microsoft.sqlserver.jdbc.SQLServerDataSource"
   :oracle     "oracle.jdbc.pool.OracleDataSource"
   :pgjdbc-ng  "com.impossibl.postgres.jdbc.PGDataSource"
   :postgresql "org.postgresql.ds.PGSimpleDataSource"
   :sybase     "com.sybase.jdbcx.SybDataSource"})

(defn datasource-config
  ""
  [datasource-options]
  (let [config (HikariConfig.)
        options               (merge default-datasource-options datasource-options)
        auto-commit           (:auto-commit options)
        read-only             (:read-only options)
        connection-timeout    (:connection-timeout options)
        idle-timeout          (:idle-timeout options)
        max-lifetime          (:max-lifetime options)
        minimum-idle          (:minimum-idle options)
        maximum-pool-size     (:maximum-pool-size options)
        adapter               (:adapter options)
        datasource-class-name (get adapters-to-datasource-class-names adapter)
        username              (:username options)
        password              (:password options)
        database-name         (:database-name options)
        server-name           (:server-name options)
        port                  (:port options)]
    (.setAutoCommit          config auto-commit)
    (.setReadOnly            config read-only)
    (.setConnectionTimeout   config connection-timeout)
    (.setIdleTimeout         config idle-timeout)
    (.setMaxLifetime         config max-lifetime)
    (.setMinimumIdle         config minimum-idle)
    (.setMaximumPoolSize     config maximum-pool-size)
    (.setDataSourceClassName config datasource-class-name)
    (if username      (.setUsername config username))
    (if password      (.setPassword config password))
    (if database-name (.addDataSourceProperty config "databaseName" database-name))
    (if server-name   (.addDataSourceProperty config "serverName"   server-name))
    (if port          (.addDataSourceProperty config "portNumber"   port))
    config))

(defn datasource-from-config
  ""
  [config]
  (let [datasource (HikariDataSource. config)] datasource))

(defn close-datasource
  ""
  [datasource]
  (.close datasource))
