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
   (let [{:keys [db
                 recreate?
                 install-schema?
                 install-index] :as m}
         (merge conn/+defaults+ m)

         conn (conn/connect m)
         _    (if recreate?
                (db/drop-db conn db))
         _    (db/create-db conn db)]
     (-> (common/map->Rethink (dissoc m :recreate? :install-schema? :install-index?))
         (assoc :conn conn)))))

(defn insert!
  [{:keys [conn db create-table]} table data opts]
  (if (and create-table
           (not ((table/list-tables conn db) table)))
    (table/create-table conn db table opts))
  (-> common/r
      (.table (common/to-string table))
      (.insert (common/from-clj data))
      (.run conn)
      common/to-clj))
