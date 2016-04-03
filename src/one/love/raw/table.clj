(ns one.love.raw.table
  (:require [one.love.common :as one]
            [hara.string.case :as case])
  (:import com.rethinkdb.net.Connection))

(defn list-tables [^Connection conn db]
  (let [db (one/to-string db)]
    (set (one/run [conn db] (.tableList)))))

(defn create-table
  "creates a table in a database; if exists, do nothing
       (one/do-> conn
         (db/create-db \"test\")
         (create-table \"test\" \"love\"))
       (list-tables conn \"test\")
       => (contains #{\"love\"} :in-any-order)"
  {:added "0.1"}
  ([^Connection conn db table]
   (create-table conn db table nil))
  ([^Connection conn db table opts]
   (let [db (one/to-string db)
         table (one/to-string table)]
     (if-not ((list-tables conn db) table)
       (one/run [conn db]
         (.tableCreate table)
         (one/merge-opts opts))
       {:tables-created 0}))))

(defn drop-table
  "drops a table in the database
       (one/do-> conn
         (db/create-db \"test\")
         (clear-tables \"test\")
         (create-table \"test\" \"love\")
         (create-table \"test\" \"one\")
         (drop-table \"test\" \"one\"))
       (list-tables conn \"test\")
       => #{\"love\"}"
  {:added "0.1"} [^Connection conn db table]
  (let [db (one/to-string db)
        table (one/to-string table)]
    (if ((list-tables conn db) table)
      (one/run [conn db]
       (.tableDrop table))
      {:tables-dropped 0})))

(defn clear-tables
  "clears all tables in the database
       (one/do-> conn
         (db/create-db \"test\")
         (clear-tables \"test\"))
       (list-tables conn \"test\")
       => #{}"
  {:added "0.1"} [^Connection conn db]
  (->> (list-tables conn db)
       (mapv (partial drop-table conn db))))

(defn rename-table
  "renames a table to a new value
       (one/do-> conn
         (db/create-db \"test\")
         (clear-tables \"test\")
         (create-table \"test\" \"one\")
         (rename-table \"test\" \"one\" \"love\"))
       (list-tables conn \"test\")
       => #{\"love\"}"
  {:added "0.1"} [^Connection conn db old-table new-table]
  (one/run [conn db old-table]
    (.config)
    (.update {"name" new-table})))

(defn info-table
  "gets information of the table in the database
       (one/do-> conn
         (db/create-db \"test\")
         (clear-tables \"test\")
         (create-table \"test\" \"love\"))
       (info-table conn \"test\" \"love\")
       => (contains
           {:shards vector?
            :indexes [],
            :durability \"hard\",
            :write-acks \"majority\",
           :name \"love\",
            :id string?
            :primary-key \"id\",
            :db \"test\"})"
  {:added "0.1"} [^Connection conn db table]
  (one/run [conn db table] (.config)))

(defn reconfigure-table
  "reconfigures table to specified value
       (one/do-> conn
         (db/create-db \"test\")
         (clear-tables \"test\")
         (create-table \"test\" \"love\")
         (reconfigure-table \"test\" \"love\"
                            {:shards 4 :replicas 1}))
       (info-table conn \"test\" \"love\")
       => (contains
           {:shards #(-> % count (= 4))
            :indexes [],
           :durability \"hard\",
            :write-acks \"majority\",
            :name \"love\",
            :id string?
            :primary-key \"id\",
            :db \"test\"})"
  {:added "0.1"} [^Connection conn db table opts]
  (one/run [conn db table]
    (.reconfigure)
    (one/merge-opts opts (comp one/to-string case/snake-case))))
