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
        datasource-class-name (:datasource-class-name options)
        minimum-idle          (:minimum-idle options)
        maximum-pool-size     (:maximum-pool-size options)
        username              (:username options)
        password              (:password options)
        database-name         (:database-name options)
        server-name           (str (:server-name options))
        port                  (str (:port options))
        server-name-with-port (clojure.string/join [server-name ":" port])]
    (.setDataSourceClassName config datasource-class-name)
    (.setAutoCommit          config auto-commit)
    (.setReadOnly            config read-only)
    (.setConnectionTimeout   config connection-timeout)
    (.setIdleTimeout         config idle-timeout)
    (.setMaxLifetime         config max-lifetime)
    (.setMinimumIdle         config minimum-idle)
    (.setMaximumPoolSize     config maximum-pool-size)
    (.addDataSourceProperty  config "user"         username)
    (.addDataSourceProperty  config "password"     password)
    (.addDataSourceProperty  config "databaseName" database-name)
    (.addDataSourceProperty  config "serverName"   server-name-with-port)
    config))

(defn datasource-from-config
  ""
  [config]
  (let [datasource (HikariDataSource. config)] datasource))

(defn close-datasource
  ""
  [datasource]
  (.close datasource))
