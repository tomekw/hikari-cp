(ns hikari-cp.core-test
  (:require [hikari-cp.core :refer :all])
  (:use expectations))

(def datasource-config-with-defaults
  (datasource-config {}))

(expect true    (.isAutoCommit         datasource-config-with-defaults))
(expect false   (.isReadOnly           datasource-config-with-defaults))
(expect 30000   (.getConnectionTimeout datasource-config-with-defaults))
(expect 600000  (.getIdleTimeout       datasource-config-with-defaults))
(expect 1800000 (.getMaxLifetime       datasource-config-with-defaults))
(expect 10      (.getMinimumIdle       datasource-config-with-defaults))
(expect 10      (.getMaximumPoolSize   datasource-config-with-defaults))

(def datasource-config-with-overrides
  (datasource-config {:auto-commit        false
                      :read-only          true
                      :connection-timeout 100
                      :idle-timeout       1
                      :max-lifetime       1
                      :minimum-idle       1
                      :maximum-pool-size  1}))

(expect false (.isAutoCommit         datasource-config-with-overrides))
(expect true  (.isReadOnly           datasource-config-with-overrides))
(expect 100   (.getConnectionTimeout datasource-config-with-overrides))
(expect 1     (.getIdleTimeout       datasource-config-with-overrides))
(expect 1     (.getMaxLifetime       datasource-config-with-overrides))
(expect 1     (.getMinimumIdle       datasource-config-with-overrides))
(expect 1     (.getMaximumPoolSize   datasource-config-with-overrides))

(def datasource-config-with-required-settings
  (datasource-config {:adapter       :postgresql
                      :username      "username"
                      :password      "password"
                      :database-name "database"
                      :server-name   "host-1"
                      :port          5433}))

(expect "org.postgresql.ds.PGSimpleDataSource" (.getDataSourceClassName datasource-config-with-required-settings))
(expect "username"                             (.getUsername            datasource-config-with-required-settings))
(expect "password"                             (.getPassword            datasource-config-with-required-settings))
; Quick and dirty hack to read portNumber from Properties
(expect "{portNumber=5433, databaseName=database, serverName=host-1}" (str (.getDataSourceProperties datasource-config-with-required-settings)))
