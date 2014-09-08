[![hikari-cp](http://clojars.org/hikari-cp/latest-version.svg)](http://clojars.org/hikari-cp)

# hikari-cp [![Build Status](https://secure.travis-ci.org/tomekw/hikari-cp.png)](http://travis-ci.org/tomekw/hikari-cp)

A Clojure wrapper to [HikariCP](https://github.com/brettwooldridge/HikariCP) - "zero-overhead" production ready JDBC connection pool.

## Future plans

* Add proper documentation
* Use [Prismatic/schema](https://github.com/Prismatic/schema) to
  validate the configuration options
* Handle configuration errors
* Support Heroku's `DATABASE_URL` with `datasource-from-url`

## Disclaimer

This library is under construction and public API is subject to change
before reaching the version `1.0.0`.

`hikari-cp` targets only Java version 8.

## Installation

Add the following dependency to your `project.clj` file:

```clj
[hikari-cp "0.3.0"]
```

## Configuration options

| Option                   | Required | Default value      | Description                                                                                                                                                                                                                                    |
| ------------------------ | :------: | ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `:auto-commit`           | No       | `true`             | This property controls the default auto-commit behavior of connections returned from the pool. It is a boolean value.                                                                                                                          |
| `:read-only`             | No       | `false`            | This property controls whether Connections obtained from the pool are in read-only mode by default.                                                                                                                                            |
| `:connection-timeout`    | No       | `30000`            | This property controls the maximum number of milliseconds that a client will wait for a connection from the pool. If this time is exceeded without a connection becoming available, a SQLException will be thrown. 100ms is the minimum value. |
| `:idle-timeout`          | No       | `600000`           | This property controls the maximum amount of time (in milliseconds) that a connection is allowed to sit idle in the pool.                                                                                                                      |
| `:max-lifetime`          | No       | `1800000`          | This property controls the maximum lifetime of a connection in the pool. A value of 0 indicates no maximum lifetime (infinite lifetime).                                                                                                       |
| `:minimum-idle`          | No       | `10`               | This property controls the minimum number of idle connections that HikariCP tries to maintain in the pool.                                                                                                                                     |
| `:maximum-pool-size`     | No       | `10`               | This property controls the maximum size that the pool is allowed to reach, including both idle and in-use connections. Basically this value will determine the maximum number of actual connections to the database backend.                   |
| `:datasource-class-name` | Yes      | None               | This is the name of the DataSource class provided by the JDBC driver. Consult the documentation for your specific JDBC driver to get this class name.                                                                                          |
| `:username`              | Yes      | None               | This property sets the default authentication username used when obtaining Connections from the underlying driver.                                                                                                                             |
| `:password`              | No       | None               | This property sets the default authentication password used when obtaining Connections from the underlying driver.                                                                                                                             |
| `:database-name`         | Yes      | None               | This property sets the database name.                                                                                                                                                                                                          |
| `:server-name`           | No       | Depends on adapter | This property sets the hostname client connects to.                                                                                                                                                                                            |
| `:port`                  | No       | Depends on adapter | This property sets the port clients connects on.                                                                                                                                                                                               |

*Please note:* All time values are specified in milliseconds.

## Adapters and corresponding datasource class names

| Adapter       | Datasource class name                           | Tested with hikari-cp |
| ------------- | ----------------------------------------------- | :-------------------: |
| `:postgresql` | `org.postgresql.ds.PGSimpleDataSource`          | Yes                   |
| `:mysql`      | `com.mysql.jdbc.jdbc2.optional.MysqlDataSource` | Yes                   |

## Usage

```clj
(ns hikari-cp.example
  (:require [hikari-cp.core :refer :all]
            [clojure.java.jdbc :as jdbc]))

(def config {:auto-commit           true
             :read-only             false
             :connection-timeout    30000
             :idle-timeout          600000
             :max-lifetime          1800000
             :minimum-idle          10
             :maximum-pool-size     10
             :datasource-class-name "org.postgresql.ds.PGSimpleDataSource"
             :username              "username"
             :password              "password"
             :database-name         "database"
             :server-name           "localhost"
             :port                  5432
             })

(def ds-config (datasource-config config))

(def datasource
  (datasource-from-config ds-config))

(defn -main [& args]
  (jdbc/with-db-connection [conn {:datasource datasource}]
    (let [rows (jdbc/query conn "SELECT * FROM table")]
      (println rows)))
  (close-datasource datasource))
```

## License

Copyright © 2014 Tomek Wałkuski

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
