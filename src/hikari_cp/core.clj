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
        datasource-class-name (:datasource-class-name options)
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
    (if username      (.addDataSourceProperty config "user"         username))
    (if password      (.addDataSourceProperty config "password"     password))
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
