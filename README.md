[![hikari-cp](http://clojars.org/hikari-cp/latest-version.svg)](http://clojars.org/hikari-cp)

# hikari-cp

A Clojure wrapper to [HikariCP](https://github.com/brettwooldridge/HikariCP) - "zero-overhead" production ready JDBC connection pool.

## Future plans

* Add proper documentation
* Write tests
* Use [Prismatic/schema](https://github.com/Prismatic/schema) to
  validate the configuration options
* Handle configuration errors
* Support Heroku's `DATABASE_URL` with `datasource-from-url`

## Disclaimer

This library is under construction and public API is subject to change
before reaching the version `1.0.0`.

## Installation

Add the following dependency to your `project.clj` file:

```clj
[hikari-cp "0.2.0"]
```

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
             :datasource-class-name "org.postgresql.ds.PGSimpleDataSource"
             :minimum-idle          10
             :maximum-pool-size     10
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
