(ns one.love.classic
  (:require [one.love.common :as common]
            [one.love.raw
             [connection :as conn]
             [db :as db]
             [table :as table]
             [system :as system]]))

(defn connect!
  ([] (connect! {}))
  ([m]
   (let [{:keys [db options] :as m} (merge conn/+defaults+ m)
         {:keys [recreate-db
                 install-schema
                 install-index]} options
         conn (conn/connect m)
         _    (if recreate-db
                (db/drop-db conn db))
         _    (db/create-db conn db)]
     (-> (common/map->Rethink m)
         (assoc :conn conn)))))

(defn insert!
  [{:keys [conn db options]} table data opts]
  (if (and (:create-table options)
           (not ((table/list-tables conn db) table)))
    (table/create-table conn db table opts))
  (-> common/r
      (.table (common/to-string table))
      (.insert (common/from-clj data))
      (.run conn)
      common/to-clj))
