(ns one.love.raw.table
  (:require [one.love.common :as common])
  (:import com.rethinkdb.net.Connection))

(defn list-tables [^Connection conn db]
  (let [db (common/to-string db)]
    (-> common/r
        (.db db)
        (.tableList)
        (.run conn)
        set)))

(defn create-table
  ([^Connection conn db table]
   (create-table conn db table nil))
  ([^Connection conn db table opts]
   (let [db (common/to-string db)
         table (common/to-string table)]
     (if-not ((list-tables conn db) table)
       (-> common/r
           (.db db)
           (.tableCreate table)
           (common/merge-opts opts)
           (.run conn)
           common/to-clj)
       {:tables-created 0}))))

(defn drop-table [^Connection conn db table]
  (let [db (common/to-string db)
        table (common/to-string table)]
    (if((list-tables conn db) table)
      (-> common/r
          (.db db)
          (.tableDrop table)
          (.run conn)
          common/to-clj)
      {:tables-dropped 0})))

(defn clear-tables [^Connection conn db]
  (->> (list-tables conn db)
       (mapv (partial drop-table conn db))))

(defn rename-table [^Connection conn db old-table new-table]
  (-> common/r
      (.db db)
      (.table old-table)
      (.config)
      (.update {"name" new-table})
      (.run conn)
      common/to-clj))

(defn info-table [^Connection conn db table]
  (-> common/r
      (.db db)
      (.table table)
      (.config)
      (.run conn)
      common/to-clj))

(defn configure-table [^Connection conn db table opts]
  (-> common/r
      (.db db)
      (.table table)
      (.config)
      (.run conn)
      common/to-clj))




