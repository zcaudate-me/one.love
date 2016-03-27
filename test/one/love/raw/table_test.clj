(ns one.love.raw.table-test
  (:use midje.sweet)
  (:require [one.love.raw
             [connection :as conn]
             [db :as db]
             [table :refer :all]]))

^{:refer one.love.raw/list-tables :added "0.1"}
(fact "lists all tables in a database"
      (let [conn (conn/connect conn/+defaults+)]
        (doto conn
          (db/create-db "test")
          (clear-tables "test")
          (create-table "test" "love")
          (create-table "test" "one"))
        (list-tables conn "test"))
      => #{"love" "one"})

^{:refer one.love.raw.table/create-table :added "0.1"}
(fact "creates a table in a database; if exists, do nothing"
      (let [conn (conn/connect conn/+defaults+)]
        (doto conn
          (db/create-db "test")
          (create-table "test" "love"))
        (list-tables conn "test"))
      => (contains #{"love"} :in-any-order))

^{:refer one.love.raw.table/drop-table :added "0.1"}
(fact "drops a table in the database"
      (let [conn (conn/connect conn/+defaults+)]
        (doto conn
          (db/create-db "test")
          (clear-tables "test")
          (create-table "test" "love")
          (create-table "test" "one")
          (drop-table "test" "one"))
        (list-tables conn "test"))
      => #{"love"})

^{:refer one.love.raw.table/clear-tables :added "0.1"}
(fact "clears all tables in the database"
      (let [conn (conn/connect conn/+defaults+)]
        (doto conn
          (db/create-db "test")
          (clear-tables "test"))
        (list-tables conn "test"))
      => #{})

^{:refer one.love.raw.table/rename-table :added "0.1"}
(fact "clears all tables in the database"
      (let [conn (conn/connect conn/+defaults+)]
        (doto conn
          (db/create-db "test")
          (clear-tables "test")
          (create-table "test" "one")
          (rename-table "test" "one" "love"))
        (list-tables conn "test"))
      => #{"love"})

^{:refer one.love.raw.table/info-table :added "0.1"}
(fact "gets information of the table in the database"
      (let [conn (conn/connect conn/+defaults+)]
        (doto conn
          (db/create-db "test")
          (clear-tables "test")
          (create-table "test" "love"))
        (info-table conn "test" "love"))
      => (contains
          {:shards vector?
           :indexes [],
           :durability "hard",
           :write-acks "majority",
           :name "love",
           :id string?
           :primary-key "id",
           :db "test"}))

^{:refer one.love.raw.table/configure-table :added "0.1"}
