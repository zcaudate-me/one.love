(ns one.love
  (:require [one.love.common :as one]
            [one.love.raw
             [connection :as conn]
             [db :as db]
             [table :as table]
             [system :as system]]
            [one.love.command :as command])
  (:import com.rethinkdb.net.Cursor))

(defn connect
  ([] (connect {}))
  ([m]
   (let [{:keys [db options] :as m} (merge conn/+defaults+ m)
         {:keys [recreate-db
                 install-schema
                 install-index]} options
         conn (conn/connect m)
         _    (if recreate-db
                (db/drop-db conn db))
         _    (db/create-db conn db)]
     (-> (one/map->Rethink m)
         (assoc :conn conn)))))

(defn insert!
  [{:keys [conn db options]} table data opts]
  (if (and (:create-table options)
           (not ((table/list-tables conn db) table)))
    (table/create-table conn db table opts))
  (-> one/r
      (.table (one/to-string table))
      (.insert (one/from-clj data))
      (.run conn)
      one/to-clj))

(defn process-results [results clj]
  (cond (instance? Cursor results)
        (cond->> (iterator-seq results)
          clj (map one/to-clj))

        :else
        (cond-> results
          clj one/to-clj)))

(defn gen-ast [commands]
  (-> commands
       command/prepare
       vec
       command/thread
       command/to-ast))

(defn run
  ([rt commands]
   (run rt commands {}))
  ([rt commands opts]
   (-> commands
       gen-ast
       (.run (:conn rt))
       (process-results (-> opts :clj false? not)))))




(comment
 
  (connect! {:host "localhost"
             :port 28015
             :schema {:post {:author [{:type :ref
                                       :ref {:ns :author}}]
                             :tags   [{:type :ref
                                       :ref {:ns :tag}}]}
                      :author {:name     [{}]
                               :location [{:type :ref
                                           :ref {:ns :location}}]}
                      :tag {:name [{}]}
                      :location {:name [{}]
                                 :neighbour [{:type :geo}]}}

             :tables #{:post :location}

             :embed  {:post #{:author :tags}
                      :author #{:location}}

             :index  {:post {:author_name [:author :name]}}}))
