(ns one.love
  (:require [clojure.walk :as walk]
            [cheshire.core :as json])
  (:import com.rethinkdb.RethinkDB
           com.rethinkdb.model.MapObject
           com.rethinkdb.net.Connection))

(defrecord Rethink []
  Object
  (toString [{:keys [conn] :as rt}]
    (str "#rethink" (-> (into {} rt)
                        (assoc :conn (if (.isOpen conn)
                                       :alive
                                       :dead))))))

(defmethod print-method Rethink
  [v w]
  (.write w (str v)))

(def +defaults+
  {:host "localhost"
   :port 28015
   :db "test"})

(defn connect!
  ([] (connect! {}))
  ([kargs]
   (let [{:keys [host port auth timeout cert ssl db recreate?] :as kargs}
         (merge +defaults+ kargs)
         conn (-> raw/r
                  (.connection)
                  (.hostname host)
                  (.port port))
         conn (cond-> conn
                db      (.db db)
                auth    (.authKey auth)
                timeout (.timeout timeout)
                cert    (.certFile cert)
                ssl     (.sslContext ssl))
         conn (.connect conn)
         _    (if recreate?
                (raw/drop-db conn db))
         _    (raw/create-db conn db)]
     (-> (map->Rethink (dissoc kargs :recreate?))
         (assoc :conn conn)))))

(defn insert! [{:keys [conn db]} table data opts]
  (if (and (:create-table opts)
           (not ((raw/list-tables conn db) table)))
    (raw/create-table conn db table opts))
  (-> raw/r
      (.table (raw/to-string table))
      (.insert (raw/from-clj data))
      (.run conn)
      raw/to-clj))

(def specs
  {:schema {:}

   :index {}}
  )

(comment

  (def rt (connect!))

  (raw/info-db (:conn rt) (:db rt))
  (raw/info-table (:conn rt) (:db rt) "test")

  (raw/create-table (:conn rt) "testeee" "test" {})
  (raw/create-table (:conn rt) :testeee :hello {})
  (raw/drop-table (:conn rt) "testeee" "test")
  (raw/list-tables (:conn rt) "testeee")
  (raw/list-tables (:conn rt) "test")
  
  (insert! rt :test )


  (.run (.tableCreate r "test") (:conn rt))

  (.run (.tableList r) (:conn rt))

  (to-clj (do
            (drop-db (:conn rt) "super")
            (create-db (:conn rt) "super")))

  ( (walk/keywordize-keys
     )
       "config_changes")

  (type )
  java.util.ArrayList
  (type (do
         (drop-db (:conn rt) "super")
         (create-db (:conn rt) "super")))
  java.util.HashMap
  

  (.run (.dbCreate r "super") (:conn rt))

  (.run (.dbDrop r "super") (:conn rt))



  (:schema )

  (def rt (connect! {:host "localhost"
                     :port 28015
                     :db "testeee"}))

  (.run (.dbList r) (:conn rt))
  
  (-> r
      (.dbCreate "test")
      (.run (:conn rt))))


