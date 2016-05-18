(ns hikari-cp.core-test
  (:require [hikari-cp.core :refer :all]
            [schema.core :as s])
  (:use expectations))

(def valid-options
  {:auto-commit        false
   :read-only          true
   :connection-timeout 1000
   :validation-timeout 1000
   :idle-timeout       0
   :max-lifetime       0
   :minimum-idle       0
   :maximum-pool-size  1
   :pool-name          "db-pool"
   :adapter            "postgresql"
   :username           "username"
   :password           "password"
   :database-name      "database"
   :server-name        "host-1"
   :port-number        5433})

(def datasource-config-with-required-settings
  (datasource-config (apply dissoc valid-options (keys default-datasource-options))))

(def datasource-config-with-overrides
  (datasource-config valid-options))

(def mysql-datasouurce-config
  (datasource-config (merge valid-options
                            {:adapter "mysql" :use-legacy-datetime-code false})))

(expect false
        (get (.getDataSourceProperties mysql-datasouurce-config) "useLegacyDatetimeCode"))
(expect true
        (.isAutoCommit datasource-config-with-required-settings))
(expect false
        (.isReadOnly datasource-config-with-required-settings))
(expect 30000
        (.getConnectionTimeout datasource-config-with-required-settings))
(expect 5000
        (.getValidationTimeout datasource-config-with-required-settings))
(expect 600000
        (.getIdleTimeout datasource-config-with-required-settings))
(expect 1800000
        (.getMaxLifetime datasource-config-with-required-settings))
(expect 10
        (.getMinimumIdle datasource-config-with-required-settings))
(expect 10
        (.getMaximumPoolSize datasource-config-with-required-settings))
(expect "org.postgresql.ds.PGSimpleDataSource"
        (.getDataSourceClassName datasource-config-with-required-settings))
(expect "username"
        (.getUsername datasource-config-with-required-settings))
(expect "password"
        (.getPassword datasource-config-with-required-settings))
(expect 5433
        (-> datasource-config-with-required-settings
            .getDataSourceProperties
            (get "portNumber")))

(expect false
        (.isAutoCommit datasource-config-with-overrides))
(expect true
        (.isReadOnly datasource-config-with-overrides))
(expect 1000
        (.getConnectionTimeout datasource-config-with-overrides))
(expect 1000
        (.getValidationTimeout datasource-config-with-overrides))
(expect 0
        (.getIdleTimeout datasource-config-with-overrides))
(expect 0
        (.getMaxLifetime datasource-config-with-overrides))
(expect 0
        (.getMinimumIdle datasource-config-with-overrides))
(expect 1
        (.getMaximumPoolSize datasource-config-with-overrides))
(expect "db-pool"
        (.getPoolName datasource-config-with-overrides))

(expect IllegalArgumentException
        (datasource-config (dissoc valid-options :adapter)))
(expect "Invalid configuration options: (:adapter)"
        (try
          (datasource-config (validate-options (dissoc valid-options :adapter)))
          (catch IllegalArgumentException e
            (str (.getMessage e)))))

(expect "jdbc:postgres:test"
        (.getJdbcUrl (datasource-config {:jdbc-url "jdbc:postgres:test"})))

(expect map?
        (validate-options valid-options))
(expect IllegalArgumentException
        (validate-options (merge valid-options {:auto-commit 1})))
(expect IllegalArgumentException
        (validate-options (merge valid-options {:read-only 1})))
(expect IllegalArgumentException
        (validate-options (merge valid-options {:connection-timeout "foo"})))
(expect IllegalArgumentException
        (validate-options (merge valid-options {:connection-timeout 999})))
(expect IllegalArgumentException
        (validate-options (merge valid-options {:validation-timeout 999})))
(expect IllegalArgumentException
        (validate-options (merge valid-options {:idle-timeout -1})))
(expect IllegalArgumentException
        (validate-options (merge valid-options {:max-lifetime -1})))
(expect IllegalArgumentException
        (validate-options (merge valid-options {:minimum-idle -1})))
(expect IllegalArgumentException
        (validate-options (merge valid-options {:maximum-pool-size -1})))
(expect IllegalArgumentException
        (validate-options (merge valid-options {:maximum-pool-size 0})))
(expect IllegalArgumentException
        (validate-options (merge valid-options {:adapter :foo})))
(expect map?
        (validate-options (merge valid-options {:username nil})))
(expect map?
        (validate-options (dissoc valid-options :username)))
(expect map?
        (validate-options (dissoc valid-options :password)))
(expect map?
        (validate-options (merge valid-options {:password nil})))
(expect map?
        (validate-options (merge valid-options {:database-name nil})))
(expect map?
        (validate-options (dissoc valid-options :database-name)))
(expect map?
        (validate-options (dissoc valid-options :server-name)))
(expect map?
        (validate-options (merge valid-options {:server-name nil})))
(expect map?
        (validate-options (merge valid-options {:port-number -1})))
(expect map?
        (validate-options (dissoc valid-options :port-number)))


;; -- check leak detections option
;; default should stay 0
(expect 0 (-> valid-options
              (datasource-config)
              (.getLeakDetectionThreshold)))

;; it should apply a correct value
(let [config (datasource-config (assoc valid-options :leak-detection-threshold 3000))]
  (expect 3000 (.getLeakDetectionThreshold config)))

;; it should complain, that value is too small
(expect IllegalArgumentException
  (validate-options (assoc valid-options :leak-detection-threshold 1)))
(expect IllegalArgumentException
  (validate-options (assoc valid-options :leak-detection-threshold 1999)))

;; Ensure that core options aren't being set as datasource properties
(expect #{"portNumber" "databaseName" "serverName"}
  (set (keys (.getDataSourceProperties datasource-config-with-required-settings))))

