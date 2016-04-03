(ns one.love.raw.db
  (:require [one.love.common :as one])
  (:import com.rethinkdb.net.Connection))

(defn list-dbs
  "lists all dbs for the connection
       (one/do-> conn
         (clear-dbs)
         (create-db \"test\")
         (create-db \"love\")
         (list-dbs))
       => #{\"love\" \"test\"}"
  {:added "0.1"} [^Connection conn]
  (-> (one/run* [conn] (.dbList)) set (disj "rethinkdb")))

(defn create-db
  "creates a db for the connection
       (one/do-> conn
         (clear-dbs)
         (create-db \"test\"))
       => (contains {:dbs-created 1})
 
       (list-dbs conn)
       => #{\"test\"}"
  {:added "0.1"} [^Connection conn db]
  (let [db (one/to-string db)]
    (if ((list-dbs conn) db)
      {:dbs-created 0}
      (one/run* [conn] (.dbCreate db)))))

(defn drop-db
  "drops the db for the connection
       (one/do-> conn
         (clear-dbs)
         (create-db \"test\")
         (create-db \"love\")
         (drop-db \"test\"))
       => (contains {:tables-dropped 0, :dbs-dropped 1})
 
       (list-dbs conn)
       => #{\"love\"}"
  {:added "0.1"} [^Connection conn db]
  (let [db (one/to-string db)]
    (if-not ((list-dbs conn) db)
      {:dbs-dropped 0}
      (one/run* [conn] (.dbDrop db)))))

(defn clear-dbs
  "clears all the current databases in the connection
       (one/do-> conn
         (clear-dbs)
         (list-dbs))
       => #{}"
  {:added "0.1"} [^Connection conn]
  (->> (list-dbs conn)
       (mapv (partial drop-db conn))))

(defn rename-db
  "renames a database in the connection
       (one/do-> conn
         (clear-dbs)
         (create-db \"test\")
         (rename-db \"test\" \"love\"))
       => {:deleted 0, :inserted 0, :unchanged 0, :replaced 1, :errors 0, :skipped 0}
       (do
         (Thread/sleep 1000)
         (list-dbs conn))
       => #{\"love\"}"
  {:added "0.1"} [^Connection conn old-db new-db]
  (one/run* [conn old-db]
    (.config)
    (.update {"name" new-db})))

(defn info-db
  "gets the config associated with the database
       (one/do-> conn
         (clear-dbs)
         (create-db \"test\")
         (info-db \"test\"))
       => (just {:name \"test\", :id string?})"
  {:added "0.1"} [^Connection conn db]
  (one/run* [conn db] (.config)))
