[![hikari-cp](http://clojars.org/hikari-cp/latest-version.svg)](http://clojars.org/hikari-cp)

# hikari-cp [![Circle CI](https://circleci.com/gh/tomekw/hikari-cp/tree/master.png?style=badge&circle-token=a17dfed149321f3a2e9b9af11cea96b9003a047a)](https://circleci.com/gh/tomekw/hikari-cp/tree/master)

A Clojure wrapper to [HikariCP](https://github.com/brettwooldridge/HikariCP) - "zero-overhead" production ready JDBC connection pool.

## Installation

Add the following dependency to your `project.clj` file:

```clojure
[hikari-cp "2.0.0"]
```

`hikari-cp` version `2.x` targets Clojure `1.9`. Version `1.8.3` is the last release for Clojure `1.8`.

Note that `hikari-cp` requires Java 8 or newer. Older versions are not supported.

You'll also need to add the JDBC driver needed for your database.

## Configuration options

| Option                      | Required | Default value          | Description                                                                                                                                                                                                                                                                                                                            |
| --------------------------- | :------: | ---------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `:auto-commit`              | No       | `true`                 | This property controls the default auto-commit behavior of connections returned from the pool. It is a boolean value.                                                                                                                                                                                                                  |
| `:read-only`                | No       | `false`                | This property controls whether Connections obtained from the pool are in read-only mode by default.                                                                                                                                                                                                                                    |
| `:connection-timeout`       | No       | `30000`                | This property controls the maximum number of milliseconds that a client will wait for a connection from the pool. If this time is exceeded without a connection becoming available, a SQLException will be thrown. 1000ms is the minimum value.                                                                                        |
| `:validation-timeout`       | No       | `5000`                 | This property controls the maximum amount of time that a connection will be tested for aliveness. This value must be less than the `:connection-timeout`. The lowest accepted validation timeout is 1000ms (1 second).                                                                                                                 |
| `:idle-timeout`             | No       | `600000`               | This property controls the maximum amount of time (in milliseconds) that a connection is allowed to sit idle in the pool.                                                                                                                                                                                                              |
| `:max-lifetime`             | No       | `1800000`              | This property controls the maximum lifetime of a connection in the pool. A value of 0 indicates no maximum lifetime (infinite lifetime).                                                                                                                                                                                               |
| `:minimum-idle`             | No       | `10`                   | This property controls the minimum number of idle connections that HikariCP tries to maintain in the pool.                                                                                                                                                                                                                             |
| `:maximum-pool-size`        | No       | `10`                   | This property controls the maximum size that the pool is allowed to reach, including both idle and in-use connections. Basically this value will determine the maximum number of actual connections to the database backend.                                                                                                           |
| `:pool-name`                | No       | Auto-generated         | This property represents a user-defined name for the connection pool and appears mainly in logging and JMX management consoles to identify pools and pool configurations.                                                                                                                                                              |
| `:jdbc-url`                 | **Yes¹** | None                   | This property sets the JDBC connection URL. Please note the `h2` adapter expects `:url` instead of `:jdbc-url`.                                                                                                                                                                                                                        |
| `:driver-class-name`        | No       | None                   | This property sets the JDBC driver class.                                                                                                                                                                                                                                                                                              |
| `:adapter`                  | **Yes¹** | None                   | This property sets the database adapter. Please check [Adapters and corresponding datasource class names](#adapters-and-corresponding-datasource-class-names) for the full list of supported adapters and their datasource class names.                                                                                                |
| `:username`                 | No       | None                   | This property sets the default authentication username used when obtaining Connections from the underlying driver.                                                                                                                                                                                                                     |
| `:password`                 | No       | None                   | This property sets the default authentication password used when obtaining Connections from the underlying driver.                                                                                                                                                                                                                     |
| `:database-name`            | No       | None                   | This property sets the database name.                                                                                                                                                                                                                                                                                                  |
| `:server-name`              | No       | Depends on the adapter | This property sets the hostname client connects to.                                                                                                                                                                                                                                                                                    |
| `:port-number`              | No       | Depends on the adapter | This property sets the port clients connects on.                                                                                                                                                                                                                                                                                       |
| `:connection-test-query`    | No       | None                   | If your driver supports JDBC4 we strongly recommend not setting this property. This is for "legacy" databases that do not support the JDBC4 `Connection.isValid()` API. This is the query that will be executed just before a connection is given to you from the pool to validate that the connection to the database is still alive. |
| `:leak-detection-threshold` | No       | 0                      | This property controls the amount of time that a connection can be out of the pool before a message is logged indicating a possible connection leak. A value of 0 means leak detection is disabled, minimum accepted value is 2000 (ms). ( *ps: it's rarely needed option, use only for debugging* )                                   |
| `:register-mbeans`          | No       | false                  | This property register mbeans which can be used in jmx to monitor hikari-cp.                                                                                                                                                                                                                                                           |
| `:connection-init-sql`      | No       | None                   | This property sets a SQL statement that will be executed after every new connection creation before adding it to the pool.                                                                                                                                                                                                             |
| `:datasource`               | No       | None                   | This property allows you to directly set the instance of the DataSource to be wrapped by the pool.                                                                                                                                                                                                                                     |
| `:datasource-class-name`    | No       | None                   | This is the name of the DataSource class provided by the JDBC driver.                                                                                                                                                                                                                                                                  |
| `:metric-registry`          | No       | None                   | This is an instance of a Dropwizard metrics MetricsRegistry.                                                                                                                                                                                                                                                                           |
| `:health-check-registry`    | No       | None                   | This is an instance of a Dropwizard metrics HealthCheckRegistry.                                                                                                                                                                                                                                                                       |

**¹** `:adapter` and `:jdbc-url` are mutually exclusive.

You can also add other datasource-specific configuration options.
By default, keywords will be converted to the camelCase format add added
as a datasource property, unless a special translation is defined:

```clojure
;; {:tcp-keep-alive true} will be:
(.addDataSourceProperty config "tcpKeepAlive" true)

;; {:use-ssl false} has a custom translation, so it will be:
(.addDataSourceProperty config "useSSL" false)
```

Custom translations of properties can be added by extending the
`translate-property` multimethod.

**Please note:** All time values are specified in milliseconds.

## Adapters and corresponding datasource class names

| Adapter          | Datasource class name                              | Tested with hikari-cp |
| ---------------- | -------------------------------------------------- | :-------------------: |
| `derby`          | `org.apache.derby.jdbc.ClientDataSource`           | No                    |
| `firebird`       | `org.firebirdsql.pool.FBSimpleDataSource`          | No                    |
| `db2`            | `com.ibm.db2.jcc.DB2SimpleDataSource`              | No                    |
| `h2`             | `org.h2.jdbcx.JdbcDataSource`                      | **Yes**               |
| `hsqldb`         | `org.hsqldb.jdbc.JDBCDataSource`                   | No                    |
| `mariadb`        | `org.mariadb.jdbc.MySQLDataSource`                 | No                    |
| `mysql`          | `com.mysql.jdbc.jdbc2.optional.MysqlDataSource`    | **Yes**               |
| `sqlserver-jtds` | `net.sourceforge.jtds.jdbcx.JtdsDataSource`        | **Yes**               |
| `sqlserver`      | `com.microsoft.sqlserver.jdbc.SQLServerDataSource` | **Yes**               |
| `oracle`         | `oracle.jdbc.pool.OracleDataSource`                | **Yes**               |
| `pgjdbc-ng`      | `com.impossibl.postgres.jdbc.PGDataSource`         | No                    |
| `postgresql`     | `org.postgresql.ds.PGSimpleDataSource`             | **Yes**               |
| `fdbsql`         | `com.foundationdb.sql.jdbc.ds.FDBSimpleDataSource` | No                    |
| `sybase`         | `com.sybase.jdbc4.jdbc.SybDataSource`              | Yes                   |

## Usage

### PostgreSQL example

```clojure
(ns hikari-cp.example
  (:require [hikari-cp.core :refer :all]
            [clojure.java.jdbc :as jdbc]))

(def datasource-options {:auto-commit        true
                         :read-only          false
                         :connection-timeout 30000
                         :validation-timeout 5000
                         :idle-timeout       600000
                         :max-lifetime       1800000
                         :minimum-idle       10
                         :maximum-pool-size  10
                         :pool-name          "db-pool"
                         :adapter            "postgresql"
                         :username           "username"
                         :password           "password"
                         :database-name      "database"
                         :server-name        "localhost"
                         :port-number        5432
                         :register-mbeans    false})

(def datasource
  (make-datasource datasource-options))

(defn -main [& args]
  (jdbc/with-db-connection [conn {:datasource datasource}]
    (let [rows (jdbc/query conn "SELECT 0")]
      (println rows)))
  (close-datasource datasource))
```

### H2 minimal config example

```clojure
(def datasource-options {:adapter "h2"
                         :url     "jdbc:h2:~/test"})
```

### Oracle example

```clojure
(ns hikari-cp.example
  (:require [hikari-cp.core :refer :all]
            [clojure.java.jdbc :as jdbc]))

(def datasource-options {:username      "username"
                         :password      "password"
                         :database-name "database"
                         :server-name   "localhost"
                         :port-number   1521
                         :driverType    "thin"       ; Other options are oci8, oci or kprb. See jdbc.pool.OracleDataSource.makeURL()
                         :adapter       "oracle"})

(def datasource
  (make-datasource datasource-options))

(defn -main [& args]
  (jdbc/with-db-connection [conn {:datasource datasource}]
    (let [rows (jdbc/query conn "SELECT 0 FROM dual")]
      (println rows)))
  (close-datasource datasource))
```

### Sybase example

```clojure
(ns hikari-cp.example
  (:require [hikari-cp.core :refer :all]
            [clojure.java.jdbc :as jdbc]))

(def datasource-options {:username      "username"
                         :password      "password"
                         :database-name "database"
                         :server-name   "localhost"
                         :port-number   5900
                         :adapter       "sybase"}) ; assumes jconn4.jar (com.sybase.jdbc4.jdbc.SybDataSource)

; if you're using jconn2.jar or jconn3.jar replace :adapter "sybase" with the following
; :datasource-class-name "com.sybase.jdbc3.jdbc.SybDataSource"
; or
; :datasource-class-name "com.sybase.jdbc2.jdbc.SybDataSource"

(def datasource
  (make-datasource datasource-options))

(defn -main [& args]
  (jdbc/with-db-connection [conn {:datasource datasource}]
    (let [rows (jdbc/query conn "SELECT 0")]
      (println rows)))
  (close-datasource datasource))
```

### SQLite example

```clojure
(def datasource-options {:jdbc-url "jdbc:sqlite:db/database.db"})
```

### Notice

`make-datasource` will throw `IllegalArgumentException` when invalid
arguments are provided:

```clojure
(make-datasource (dissoc config :username :database-name))
;; IllegalArgumentException: Invalid configuration options: (:username :database-name)
```

## License

Copyright © 2014 - 2017 [Tomek Wałkuski](https://github.com/tomekw) and [Jan Stępień](https://github.com/jstepien)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
