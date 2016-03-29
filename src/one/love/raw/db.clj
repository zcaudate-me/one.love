(ns one.love.raw.db
  (:require [one.love.common :as one])
  (:import com.rethinkdb.net.Connection))

(defn list-dbs [^Connection conn]
  (-> (one/run [conn] (.dbList)) set (disj "rethinkdb")))

(defn create-db [^Connection conn db]
  (let [db (one/to-string db)]
    (if ((list-dbs conn) db)
      {:dbs-created 0}
      (one/run [conn] (.dbCreate db)))))

(defn drop-db [^Connection conn db]
  (let [db (one/to-string db)]
    (if-not ((list-dbs conn) db)
      {:dbs-dropped 0}
      (one/run [conn] (.dbDrop db)))))

(defn clear-dbs [^Connection conn]
  (->> (list-dbs conn)
       (mapv (partial drop-db conn))))

(defn rename-db [^Connection conn old-db new-db]
  (one/run [conn old-db]
    (.config)
    (.update {"name" new-db})))

(defn info-db [^Connection conn db]
  (one/run [conn db] (.config)))
