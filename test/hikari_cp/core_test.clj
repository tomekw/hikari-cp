(ns hikari-cp.core-test
  (:require [clojure.test :refer :all]
            [hikari-cp.core :refer :all])
  (:import (com.zaxxer.hikari.pool HikariPool$PoolInitializationException)
           (com.zaxxer.hikari HikariConfig HikariDataSource)
           (com.codahale.metrics MetricRegistry)
           (com.codahale.metrics.health HealthCheckRegistry)
           (com.zaxxer.hikari.metrics.prometheus PrometheusMetricsTrackerFactory)))

(def valid-options
  {:allow-pool-suspension    true
   :auto-commit              false
   :read-only                true
   :connection-timeout       1000
   :validation-timeout       1000
   :idle-timeout             0
   :max-lifetime             0
   :minimum-idle             0
   :maximum-pool-size        1
   :pool-name                "db-pool"
   :adapter                  "postgresql"
   :username                 "username"
   :password                 "password"
   :database-name            "database"
   :server-name              "host-1"
   :port-number              5433
   :connection-init-sql      "set join_collapse_limit=4"
   :connection-test-query    "select 0"
   :register-mbeans          true
   :transaction-isolation    "TRANSACTION_SERIALIZABLE"})

(def alternate-valid-options
  {:driver-class-name "org.postgresql.Driver"
   :jdbc-url          "jdbc:postgresql://localhost:5433/test"})

(def alternate-valid-options2
  {:datasource-class-name "com.sybase.jdbc3.jdbc.SybDataSource"})

(def metric-registry-options
  {:metric-registry (MetricRegistry.)})

(def health-check-registry-options
  {:health-check-registry (HealthCheckRegistry.)})

(def metrics-tracker-factory-options
  {:metrics-tracker-factory (PrometheusMetricsTrackerFactory.)})

(def datasource-config-with-required-settings
  (datasource-config (apply dissoc valid-options (keys default-datasource-options))))

(def datasource-config-with-overrides
  (datasource-config valid-options))

(def datasource-config-with-overrides-alternate
  (datasource-config (-> (dissoc valid-options :adapter)
                         (merge alternate-valid-options))))

(def datasource-config-with-overrides-alternate2
  (datasource-config (-> (dissoc valid-options :adapter)
                         (merge alternate-valid-options2))))

(def mysql8-datasource-config
  (datasource-config (merge valid-options {:adapter "mysql8"})))

(def mysql-datasource-config
  (datasource-config (merge valid-options
                            {:adapter "mysql"
                             :use-legacy-datetime-code false})))

(def metric-registry-config (datasource-config (merge valid-options metric-registry-options)))

(def health-check-registry-config (datasource-config (merge valid-options health-check-registry-options)))

(def metrics-tracker-factory-config (datasource-config (merge valid-options metrics-tracker-factory-options)))

(deftest mysql-datasource-properties-test
  (testing "MySQL datasource uses legacy datetime code false"
    (is (= false (get (.getDataSourceProperties ^HikariConfig mysql-datasource-config) "useLegacyDatetimeCode"))))

  (testing "MySQL datasource class name"
    (is (= "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"
           (.getDataSourceClassName ^HikariConfig mysql-datasource-config))))

  (testing "MySQL8 datasource class name"
    (is (= "com.mysql.cj.jdbc.MysqlDataSource"
           (.getDataSourceClassName ^HikariConfig mysql8-datasource-config)))))

(deftest datasource-config-with-required-settings-test
  (testing "allow-pool-suspension default setting"
    (is (= false (.isAllowPoolSuspension ^HikariConfig datasource-config-with-required-settings))))

  (testing "auto-commit default setting"
    (is (= true (.isAutoCommit ^HikariConfig datasource-config-with-required-settings))))

  (testing "read-only default setting"
    (is (= false (.isReadOnly ^HikariConfig datasource-config-with-required-settings))))

  (testing "connection timeout default"
    (is (= 30000 (.getConnectionTimeout ^HikariConfig datasource-config-with-required-settings))))

  (testing "validation timeout default"
    (is (= 5000 (.getValidationTimeout ^HikariConfig datasource-config-with-required-settings))))

  (testing "idle timeout default"
    (is (= 600000 (.getIdleTimeout ^HikariConfig datasource-config-with-required-settings))))

  (testing "max lifetime default"
    (is (= 1800000 (.getMaxLifetime ^HikariConfig datasource-config-with-required-settings))))

  (testing "minimum idle default"
    (is (= 10 (.getMinimumIdle ^HikariConfig datasource-config-with-required-settings))))

  (testing "maximum pool size default"
    (is (= 10 (.getMaximumPoolSize ^HikariConfig datasource-config-with-required-settings))))

  (testing "datasource class name for postgresql"
    (is (= "org.postgresql.ds.PGSimpleDataSource"
           (.getDataSourceClassName ^HikariConfig datasource-config-with-required-settings))))

  (testing "username setting"
    (is (= "username" (.getUsername ^HikariConfig datasource-config-with-required-settings))))

  (testing "password setting"
    (is (= "password" (.getPassword ^HikariConfig datasource-config-with-required-settings))))

  (testing "port number datasource property"
    (is (= 5433 (-> ^HikariConfig datasource-config-with-required-settings
                    .getDataSourceProperties
                    (get "portNumber")))))

  (testing "metric registry default is nil"
    (is (nil? (.getMetricRegistry ^HikariConfig datasource-config-with-required-settings))))

  (testing "health check registry default is nil"
    (is (nil? (.getHealthCheckRegistry ^HikariConfig datasource-config-with-required-settings))))

  (testing "metrics tracker factory default is nil"
    (is (nil? (.getMetricsTrackerFactory ^HikariConfig datasource-config-with-required-settings))))

  (testing "transaction isolation setting"
    (is (= "TRANSACTION_SERIALIZABLE"
           (.getTransactionIsolation ^HikariConfig datasource-config-with-required-settings)))))

(deftest metric-registry-config-test
  (testing "metric registry is set when provided"
    (is (= (:metric-registry metric-registry-options)
           (.getMetricRegistry ^HikariConfig metric-registry-config)))))

(deftest health-check-registry-config-test
  (testing "health check registry is set when provided"
    (is (= (:health-check-registry health-check-registry-options)
           (.getHealthCheckRegistry ^HikariConfig health-check-registry-config)))))

(deftest metrics-tracker-factory-config-test
  (testing "metrics tracker factory is set when provided"
    (is (= (:metrics-tracker-factory metrics-tracker-factory-options)
           (.getMetricsTrackerFactory ^HikariConfig metrics-tracker-factory-config)))))

(deftest datasource-config-with-overrides-test
  (testing "allow-pool-suspension override"
    (is (= true (.isAllowPoolSuspension ^HikariConfig datasource-config-with-overrides))))

  (testing "auto-commit override"
    (is (= false (.isAutoCommit ^HikariConfig datasource-config-with-overrides))))

  (testing "read-only override"
    (is (= true (.isReadOnly ^HikariConfig datasource-config-with-overrides))))

  (testing "connection timeout override"
    (is (= 1000 (.getConnectionTimeout ^HikariConfig datasource-config-with-overrides))))

  (testing "validation timeout override"
    (is (= 1000 (.getValidationTimeout ^HikariConfig datasource-config-with-overrides))))

  (testing "idle timeout override"
    (is (= 0 (.getIdleTimeout ^HikariConfig datasource-config-with-overrides))))

  (testing "max lifetime override"
    (is (= 0 (.getMaxLifetime ^HikariConfig datasource-config-with-overrides))))

  (testing "minimum idle override"
    (is (= 0 (.getMinimumIdle ^HikariConfig datasource-config-with-overrides))))

  (testing "maximum pool size override"
    (is (= 1 (.getMaximumPoolSize ^HikariConfig datasource-config-with-overrides))))

  (testing "pool name setting"
    (is (= "db-pool" (.getPoolName ^HikariConfig datasource-config-with-overrides))))

  (testing "connection init sql"
    (is (= "set join_collapse_limit=4"
           (.getConnectionInitSql ^HikariConfig datasource-config-with-overrides))))

  (testing "connection test query"
    (is (= "select 0" (.getConnectionTestQuery ^HikariConfig datasource-config-with-overrides))))

  (testing "register mbeans"
    (is (= true (.isRegisterMbeans ^HikariConfig datasource-config-with-overrides)))))

(deftest datasource-config-alternate-test
  (testing "driver class name with alternate options"
    (is (= "org.postgresql.Driver"
           (.getDriverClassName ^HikariConfig datasource-config-with-overrides-alternate))))

  (testing "jdbc url with alternate options"
    (is (= "jdbc:postgresql://localhost:5433/test"
           (.getJdbcUrl ^HikariConfig datasource-config-with-overrides-alternate)))))

(deftest datasource-config-alternate2-test
  (testing "datasource class name with alternate options 2"
    (is (= "com.sybase.jdbc3.jdbc.SybDataSource"
           (.getDataSourceClassName ^HikariConfig datasource-config-with-overrides-alternate2)))))

(deftest datasource-config-validation-errors-test
  (testing "missing adapter throws exception"
    (is (thrown? IllegalArgumentException
                 (datasource-config (dissoc valid-options :adapter)))))

  (testing "missing adapter error message"
    (is (re-find #"contains\? % :adapter"
                 (try
                   (datasource-config (validate-options (dissoc valid-options :adapter)))
                   (catch IllegalArgumentException e
                     (str (.getMessage e)))))))

  (testing "jdbc-url is used when provided"
    (is (= "jdbc:postgres:test"
           (.getJdbcUrl ^HikariConfig (datasource-config {:jdbc-url "jdbc:postgres:test"}))))))

(deftest validate-options-test
  (testing "valid options return a map"
    (is (map? (validate-options valid-options))))

  (testing "invalid allow-pool-suspension type throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:allow-pool-suspension 1})))))

  (testing "invalid auto-commit type throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:auto-commit 1})))))

  (testing "invalid read-only type throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:read-only 1})))))

  (testing "invalid connection-timeout type throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:connection-timeout "foo"})))))

  (testing "connection-timeout below minimum throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:connection-timeout 999})))))

  (testing "validation-timeout below minimum throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:validation-timeout 999})))))

  (testing "negative idle-timeout throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:idle-timeout -1})))))

  (testing "negative max-lifetime throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:max-lifetime -1})))))

  (testing "negative minimum-idle throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:minimum-idle -1})))))

  (testing "negative maximum-pool-size throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:maximum-pool-size -1})))))

  (testing "zero maximum-pool-size throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:maximum-pool-size 0})))))

  (testing "invalid adapter throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:adapter :foo})))))

  (testing "deprecated datasource-classname throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:datasource-classname "adsf"})))))

  (testing "nil jdbc-url without adapter throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge (dissoc valid-options :adapter) {:jdbc-url nil})))))

  (testing "nil driver-class-name with jdbc-url throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge (dissoc valid-options :adapter)
                                         {:jdbc-url "jdbc:h2:~/test"
                                          :driver-class-name nil})))))

  (testing "invalid transaction-isolation type throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge valid-options {:transaction-isolation 1}))))))

(deftest validate-options-optional-fields-test
  (testing "nil username is valid"
    (is (map? (validate-options (merge valid-options {:username nil})))))

  (testing "missing username is valid"
    (is (map? (validate-options (dissoc valid-options :username)))))

  (testing "missing password is valid"
    (is (map? (validate-options (dissoc valid-options :password)))))

  (testing "nil password is valid"
    (is (map? (validate-options (merge valid-options {:password nil})))))

  (testing "nil database-name is valid"
    (is (map? (validate-options (merge valid-options {:database-name nil})))))

  (testing "missing database-name is valid"
    (is (map? (validate-options (dissoc valid-options :database-name)))))

  (testing "missing server-name is valid"
    (is (map? (validate-options (dissoc valid-options :server-name)))))

  (testing "nil server-name is valid"
    (is (map? (validate-options (merge valid-options {:server-name nil})))))

  (testing "negative port-number is valid"
    (is (map? (validate-options (merge valid-options {:port-number -1})))))

  (testing "missing port-number is valid"
    (is (map? (validate-options (dissoc valid-options :port-number)))))

  (testing "jdbc-url without adapter is valid"
    (is (map? (validate-options (merge (dissoc valid-options :adapter)
                                      {:jdbc-url "jdbc:h2:~/test"})))))

  (testing "jdbc-url with driver-class-name is valid"
    (is (map? (validate-options (merge (dissoc valid-options :adapter)
                                      {:jdbc-url "jdbc:h2:~/test"
                                       :driver-class-name "org.h2.Driver"}))))))

(deftest leak-detection-threshold-test
  (testing "default leak detection threshold is 0"
    (is (= 0 (.getLeakDetectionThreshold ^HikariConfig (datasource-config valid-options)))))

  (testing "leak detection threshold can be set to valid value"
    (let [config (datasource-config (assoc valid-options :leak-detection-threshold 3000))]
      (is (= 3000 (.getLeakDetectionThreshold ^HikariConfig config)))))

  (testing "leak detection threshold below 2000 throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (assoc valid-options :leak-detection-threshold 1)))))

  (testing "leak detection threshold of 1999 throws exception"
    (is (thrown? IllegalArgumentException
                 (validate-options (assoc valid-options :leak-detection-threshold 1999))))))

(deftest core-options-not-set-as-datasource-properties-test
  (testing "only non-core options are set as datasource properties"
    (is (= #{"portNumber" "databaseName" "serverName"}
           (set (keys (.getDataSourceProperties ^HikariConfig metric-registry-config)))))))

(deftest make-datasource-test
  (testing "make-datasource throws exception with invalid connection details"
    (is (thrown? HikariPool$PoolInitializationException
                 (make-datasource valid-options)))))

(deftest translate-property-test
  (testing "tinyInt1isBit translates correctly"
    (is (= "tinyInt1isBit" (translate-property :tinyInt1isBit))))

  (testing "tiny-int1is-bit translates to tinyInt1isBit"
    (is (= "tinyInt1isBit" (translate-property :tiny-int1is-bit))))

  (testing "useSSL translates correctly"
    (is (= "useSSL" (translate-property :useSSL))))

  (testing "use-ssl translates to useSSL"
    (is (= "useSSL" (translate-property :use-ssl))))

  (testing "useFOO translates to useFoo"
    (is (= "useFoo" (translate-property :useFOO)))))

(deftest translate-property-extensibility-test
  (testing "translate-property is extensible"
    (defmethod translate-property ::extend-translate-test [_] 42)
    (is (= 42 (translate-property ::extend-translate-test)))))

(deftest driver-class-name-validation-test
  (testing "valid PostgreSQL driver class is accepted"
    (is (map? (validate-options (merge (dissoc valid-options :adapter)
                                      {:jdbc-url "jdbc:postgresql://localhost:5432/test"
                                       :driver-class-name "org.postgresql.Driver"})))))

  (testing "valid MySQL driver class is accepted"
    (is (map? (validate-options (merge (dissoc valid-options :adapter)
                                      {:jdbc-url "jdbc:mysql://localhost:3306/test"
                                       :driver-class-name "com.mysql.cj.jdbc.Driver"})))))

  (testing "DataSource class is rejected as driver class"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge (dissoc valid-options :adapter)
                                         {:jdbc-url "jdbc:postgresql://localhost:5432/test"
                                          :driver-class-name "org.postgresql.ds.PGSimpleDataSource"})))))

  (testing "non-existent driver class is rejected"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge (dissoc valid-options :adapter)
                                         {:jdbc-url "jdbc:postgresql://localhost:5432/test"
                                          :driver-class-name "com.fake.NonExistentDriver"})))))

  (testing "random class name that's not a driver is rejected"
    (is (thrown? IllegalArgumentException
                 (validate-options (merge (dissoc valid-options :adapter)
                                         {:jdbc-url "jdbc:postgresql://localhost:5432/test"
                                          :driver-class-name "java.lang.String"})))))

  (testing "is-running?"
    (let [pool (make-datasource {:adapter "h2"
                                 :url "jdbc:h2:mem:test"})]
          (is (= true (is-running? pool)))

          (close-datasource pool)

          (is (= false (is-running? pool)))))

  (testing "is-closed?"
    (let [pool (make-datasource {:adapter "h2"
                                 :url "jdbc:h2:mem:test"})]
          (is (= false (is-closed? pool)))

          (close-datasource pool)

          (is (= true (is-closed? pool))))))
