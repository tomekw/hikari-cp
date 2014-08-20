[![clojars version](https://clojars.org/hikari-cp)](https://clojars.org/hikari-cp)

# hikari-cp

A Clojure wrapper to [HikariCP](https://github.com/brettwooldridge/HikariCP) - "zero-overhead" production ready JDBC connection pool.

## Future plans

* I'm still learning Clojure and this is my first public Clojure project
* Add proper documentation
* Write tests
* Use [Prismatic/schema](https://github.com/Prismatic/schema) to
  validate the configuration options
* Handle configuration errors

I won't be available on-line until 08.09.2014!

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

(def ds-config (data-source-config config))

(def data-source
  (data-source-from-config ds-config))

(defn -main [& args]
  (jdbc/with-db-connection [conn {:datasource data-source}]
    (let [rows (jdbc/query conn "SELECT * FROM table")]
      (println rows)))
  (close-data-source data-source))
```

## License

Copyright © 2014 Tomek Wałkuski

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
