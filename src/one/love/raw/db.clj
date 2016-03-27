(ns one.love.raw.db
  (:require [one.love.common :as common])
  (:import com.rethinkdb.net.Connection))

(defn list-dbs [^Connection conn]
  (-> common/r (.dbList) (.run conn) set (disj "rethinkdb")))

(defn create-db [^Connection conn db]
  (let [db (common/to-string db)]
    (if ((list-dbs conn) db)
      {:dbs-created 0}
      (-> common/r
          (.dbCreate db) 
          (.run conn) 
          common/to-clj))))

(defn drop-db [^Connection conn db]
  (let [db (common/to-string db)]
    (if-not ((list-dbs conn) db)
      {:dbs-dropped 0}
      (-> common/r 
          (.dbDrop db) 
          (.run conn) 
          common/to-clj))))

(defn clear-dbs [^Connection conn]
  (->> (list-dbs conn)
       (mapv (partial drop-db conn))))

(defn rename-db [^Connection conn old-db new-db]
  (-> common/r 
      (.db old-db) 
      (.config) 
      (.update {"name" new-db}) 
      (.run conn) 
      common/to-clj))

(defn info-db [^Connection conn db]
  (-> common/r 
      (.db db) 
      (.config) 
      (.run conn) 
      common/to-clj))
