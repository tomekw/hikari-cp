## unreleased

* updated `HikariCP` to `3.3.0`
* Add missing `::jdbc-url-options` specs

## 2.6.0

* Added support for Mysql Connector's tinyInt1isBit property.

## 2.5.0

* updated `HikariCP` to `3.2.0`

## 2.4.0

* updated `HikariCP` to `3.1.0`

## 2.3.0

* updated `HikariCP` to `3.0.0`

## 2.2.0

* Replace Schema with clojure.spec. NOTE: internal specs are not part of
  the public API. Depending on them outside of hikari-cp is discouraged.

## 2.1.0

* add preliminary support for Neo4j

## 2.0.1

* updated `HikariCP` to `2.7.6`

## 2.0.0

* updated `Clojure` to `1.9.0`
* **BREAKING CHANGE**: rename `datasource-classname` to `datasource-class-name`

## 1.8.3

* allow custom `datasource-classname` along with `adapter`
* updated `HikariCP` to `2.7.4`

## 1.8.2

* updated `HikariCP` to `2.7.3`

## 1.8.1

* removed `:connection-test` option as it was deprecated and had no functionality backing it in `HikariCP`.
  Please use `:connection-test-query`
* updated `HikariCP` to `2.7.2`

## 1.8.0

* added support for Dropwizard healthcheck functionality
* updated `HikariCP` to `2.7.1`
* added `:connection-test` option for disabling default connection test

## 1.7.6

* updated `HikariCP` to `2.6.0`
* do not attempt to use :metric-registry as a datasource property

## 1.7.5

* added support for Dropwizard metrics registry
* added support for `useSSL` property

## 1.7.4

* updated `HikariCP` to `2.5.1`

## 1.7.3

* fixed validation for datasource

## 1.7.2

* updated `HikariCP` to `2.4.7`

## 1.7.1

* added `jdbc-url` to be a required key for datasource

## 1.7.0

* added `datasource` and `datasource-classname` options
* make `connection-init-sql` a none core option
* make `driver-class-name` optional when `jdbc-url` is given

## 1.6.1

* Call setRegisterMbeans property only when it is specified.
* Fix a bug introduced in a25b7a7f20b0a5c46dd83ea32d7a5a7b1c184273 where
  the non core options are no longer properly detected

## 1.6.0

* allow configuring without an adapter, but using a JDBC URL and driver class name
* added `connection-init-sql` option for Hikari config

## 1.5.0

* updated `HikariCP` to `2.4.3`
* added `leak-detection-threshold` option for Hikari config
* added `register-mbeans` option for Hikari config

## 1.4.0

* added `sqlite` support with `org.sqlite.JDBC` adapter
* added a general purpose way to alter Hikari config

## 1.3.1

* updated `clojure` to `1.7.0`
* updated `HikariCP` to `2.4.1`

## 1.3.0

* updated `HikariCP` to `2.4.0`

## 1.2.4

* updated `HikariCP` to `2.3.8`

## 1.2.3

* updated `HikariCP` to `2.3.7`

## 1.2.2

* updated `HikariCP` to `2.3.6`

## 1.2.1

* updated `HikariCP` to `2.3.5`

## 1.2.0

* updated `HikariCP` to `2.3.4`

## 1.1.1

* updated `HikariCP` to `2.3.2`
* fixed issue with setting datasource-specific configuration options
  to falsey values

## 1.1.0

* updated `HikariCP` to `2.3.1`
* removed `datasource-from-config` function
* added support for `:validation-timeout` configuration option

## 1.0.0

* updated `HikariCP` to `2.3.0`

## 0.13.0

* added support for FoundationDB driver (`:adapter` set to `fdbsql`)

## 0.12.1

* allowed optional configuration of `connection-test-query`

## 0.12.0

* `datasource-from-config` has been deprecated and will be removed
  in version `1.0.0`
* added `make-datasource` function. It takes options map directly as an argument
* changed the `:adapter` key value type from keyword to string

## 0.11.1

* replaced `camel-snake-kebab` with `org.tobereplaced/lettercase` to make sure
  builds using `:aot` continue to work on Mac and Windows

## 0.11.0

* updated `HikariCP` to `2.2.5`

## 0.10.0

* added `:pool-name` configuration option

## 0.9.1

* updated `HikariCP` to `2.2.4`

## 0.9.0

* updated `HikariCP` to `2.2.3`

## 0.8.0

* added validations for configuration option key names: all have to be symbols
* updated `HikariCP` to `2.1.0`

## 0.7.0

* added support for datasource-specific configuration options

## 0.6.0

* added validations for configuration options

## 0.5.0

* added support for MS SQL jTDS driver (`:adapter` set to `:sqlserver-jtds`)

## 0.4.0

* added `:adapter` configuration option instead of setting
  `:datasource-class-name`

## 0.3.0

* fixed setting `portNumber` for all adapters

## 0.2.0

* function `data-source-from-config` renamed to `datasource-from-config`
* function `close-data-source` renamed to `close-datasource`

## 0.1.0

* initial release
