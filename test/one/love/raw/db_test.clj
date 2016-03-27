(ns one.love.raw.db-test
  (:use midje.sweet)
  (:require [one.love.raw.db :refer :all]
            [one.love.raw.connection :as conn]))

^{:refer one.love.raw.db/list-dbs :added "0.1"}
(fact "lists all dbs for the connection"
      (let [conn (conn/connect conn/+defaults+)]
        (doto conn
          (clear-dbs)
          (create-db "test")
          (create-db "love"))
        (list-dbs conn))
      => #{"love" "test"})

^{:refer one.love.raw.db/create-db :added "0.1"}
(fact "creates a db for the connection"
      (let [conn (conn/connect conn/+defaults+)]
        (clear-dbs conn)
        [(create-db conn "test")
         (list-dbs conn)])
      => (contains [(contains {:dbs-created 1})
                    #{"test"}]))

^{:refer one.love.raw.db/drop-db :added "0.1"}
(fact "drops the db for the connection"
      (let [conn (conn/connect conn/+defaults+)]
        (doto conn
          (clear-dbs)
          (create-db "test")
          (create-db "love")
          (drop-db "test"))
        (list-dbs conn))
      => #{"love"})

^{:refer one.love.raw.db/clear-dbs :added "0.1"}
(fact "clears all the current databases in the connection"
      (let [conn (conn/connect conn/+defaults+)]
        (clear-dbs conn)
        (list-dbs conn))
      => #{})

^{:refer one.love.raw.db/rename-db :added "0.1"}
(fact "renames a database in the connection"
      (let [conn (conn/connect conn/+defaults+)]
        (doto conn
          (clear-dbs)
          (create-db "test")
          (rename-db "test" "love"))
        (Thread/sleep 1000)
        (list-dbs conn))
      => #{"love"})

^{:refer one.love.raw.db/info-db :added "0.1"}
(fact "gets the config associated with the database"
      (let [conn (conn/connect conn/+defaults+)]
        (doto conn
          (clear-dbs)
          (create-db "test"))
        (info-db conn "test"))
      => (just {:name "test", :id string?}))

