(ns one.love.raw.table
  (:require [one.love.common :as one]
            [hara.string.case :as case])
  (:import com.rethinkdb.net.Connection))

(defn list-tables [^Connection conn db]
  (let [db (one/to-string db)]
    (set (one/run [conn db] (.tableList)))))

(defn create-table
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

(defn drop-table [^Connection conn db table]
  (let [db (one/to-string db)
        table (one/to-string table)]
    (if ((list-tables conn db) table)
      (one/run [conn db]
       (.tableDrop table))
      {:tables-dropped 0})))

(defn clear-tables [^Connection conn db]
  (->> (list-tables conn db)
       (mapv (partial drop-table conn db))))

(defn rename-table [^Connection conn db old-table new-table]
  (one/run [conn db old-table]
    (.config)
    (.update {"name" new-table})))

(defn info-table [^Connection conn db table]
  (one/run [conn db table] (.config)))

(defn reconfigure-table [^Connection conn db table opts]
  (one/run [conn db table]
    (.reconfigure)
    (one/merge-opts opts (comp one/to-string case/snake-case))))
