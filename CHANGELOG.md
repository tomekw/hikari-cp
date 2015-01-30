* fixed setting of datasource properties to false

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

* Initial release
