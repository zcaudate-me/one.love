(ns one.love.raw.db-test
  (:use midje.sweet)
  (:require [one.love.common :as one]
            [one.love.raw.db :refer :all]
            [one.love.raw.connection :as conn]))

(def conn (conn/connect conn/+defaults+))

^{:refer one.love.raw.db/list-dbs :added "0.1"}
(fact "lists all dbs for the connection"
  (one/do-> conn
            (clear-dbs)
            (create-db "test")
            (create-db "love")
            (list-dbs))
  => #{"love" "test"})

^{:refer one.love.raw.db/create-db :added "0.1"}
(fact "creates a db for the connection"
  (one/do-> conn
            (clear-dbs)
            (create-db "test"))
  => (contains {:dbs-created 1})

  (list-dbs conn)
  => #{"test"})

^{:refer one.love.raw.db/drop-db :added "0.1"}
(fact "drops the db for the connection"
  (one/do-> conn
            (clear-dbs)
            (create-db "test")
            (create-db "love")
            (drop-db "test"))
  => (contains {:tables-dropped 0, :dbs-dropped 1})

  (list-dbs conn)
  => #{"love"})

^{:refer one.love.raw.db/clear-dbs :added "0.1"}
(fact "clears all the current databases in the connection"
  (one/do-> conn
            (clear-dbs)
            (list-dbs))
  => #{})

^{:refer one.love.raw.db/rename-db :added "0.1"}
(fact "renames a database in the connection"
  (one/do-> conn
            (clear-dbs)
            (create-db "test")
            (rename-db "test" "love"))
  => {:deleted 0, :inserted 0, :unchanged 0, :replaced 1, :errors 0, :skipped 0}
  (do
    (Thread/sleep 1000)
    (list-dbs conn))
  => #{"love"})

^{:refer one.love.raw.db/info-db :added "0.1"}
(fact "gets the config associated with the database"
  (one/do-> conn
            (clear-dbs)
            (create-db "test")
            (info-db "test"))
  => (just {:name "test", :id string?}))

 
