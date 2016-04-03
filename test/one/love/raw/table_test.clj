(ns one.love.raw.table-test
  (:use midje.sweet)
  (:require [one.love.common :as one]
            [one.love.raw
             [connection :as conn]
             [db :as db]
             [table :refer :all]]))

(def conn (conn/connect conn/+defaults+))

^{:refer one.love.raw/list-tables :added "0.1"}
(fact "lists all tables in a database"
  (one/do-> conn
            (db/create-db "test")
            (clear-tables "test")
            (create-table "test" "love")
            (create-table "test" "one"))
  (list-tables conn "test")
  => #{"love" "one"})

^{:refer one.love.raw.table/create-table :added "0.1"}
(fact "creates a table in a database; if exists, do nothing"
  (one/do-> conn
            (db/create-db "test")
            (create-table "test" "love"))
  (list-tables conn "test")
  => (contains #{"love"} :in-any-order))

^{:refer one.love.raw.table/drop-table :added "0.1"}
(fact "drops a table in the database"
  (one/do-> conn
            (db/create-db "test")
            (clear-tables "test")
            (create-table "test" "love")
            (create-table "test" "one")
            (drop-table "test" "one"))
  (list-tables conn "test")
  => #{"love"})

^{:refer one.love.raw.table/clear-tables :added "0.1"}
(fact "clears all tables in the database"
  (one/do-> conn
            (db/create-db "test")
            (clear-tables "test"))
  (list-tables conn "test")
  => #{})

^{:refer one.love.raw.table/rename-table :added "0.1"}
(fact "renames a table to a new value"
  (one/do-> conn
            (db/create-db "test")
            (clear-tables "test")
            (create-table "test" "one")
            (rename-table "test" "one" "love"))
  (list-tables conn "test")
  => #{"love"})

^{:refer one.love.raw.table/info-table :added "0.1"}
(fact "gets information of the table in the database"
  (one/do-> conn
            (db/create-db "test")
            (clear-tables "test")
            (create-table "test" "love"))
  (info-table conn "test" "love")
  => (contains
      {:shards vector?
       :indexes [],
       :durability "hard",
       :write-acks "majority",
       :name "love",
       :id string?
       :primary-key "id",
       :db "test"}))

^{:refer one.love.raw.table/reconfigure-table :added "0.1"}
(fact "reconfigures table to specified value"
  (one/do-> conn
            (db/create-db "test")
            (clear-tables "test")
            (create-table "test" "love")
            (reconfigure-table "test" "love"
                               {:shards 4 :replicas 1}))
  (info-table conn "test" "love")
  => (contains
      {:shards #(-> % count (= 4))
       :indexes [],
       :durability "hard",
       :write-acks "majority",
       :name "love",
       :id string?
       :primary-key "id",
       :db "test"}))
 
