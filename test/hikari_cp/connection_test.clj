(ns hikari-cp.connection-test
  (:require
   [expectations :refer [expect]]
   [hikari-cp.core :as hikari-cp]
   [next.jdbc :as jdbc]))

(let [pool (hikari-cp/make-datasource {:adapter "h2"
                                       :url "jdbc:h2:mem:test"
                                       ;; :register-mbeans true
                                       ;; :connection-timeout 1000
                                       ;; :connection-test-query "select 0"
                                       })]

  (with-open [connection (jdbc/get-connection pool {})]
    (let [result (jdbc/execute-one! connection ["select 1 as count"])]
      (expect {:COUNT 1} result)))

  ;; NOTE:
  ;; can't do this: (expect true (hikari-cp/is-running? pool))
  ;; most likely due to how expectations works
  (let [running? (hikari-cp/is-running? pool)
        closed? (hikari-cp/is-closed? pool)]
    (expect true running?)
    (expect false closed?))

  (hikari-cp/close-datasource pool)

  (let [running? (hikari-cp/is-running? pool)
        closed? (hikari-cp/is-closed? pool)]

    (expect false running?)
    (expect true closed?)))
