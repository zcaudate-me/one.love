(ns one.love.raw.system
  (:require [one.love.common :as common]))

(defn config
  ([conn] #{:table :server :db :cluster})
  ([conn name]
   (-> common/r
       (.db "rethinkdb")
       (.table (str (common/to-string name) "_config"))
       (.run conn)
       (->> (mapv common/to-clj))))
  ([conn name query update]
   (-> common/r
       (.db "rethinkdb")
       (.table (str (common/to-string name) "_config"))
       (.update update)
       (.run conn)
       (->> (mapv common/to-clj)))))

(def status-lookup
  {:table "table_status"
   :server "server_status"
   :issues "current_issues"
   :jobs   "jobs"})

(defn status
  ([conn] (-> status-lookup keys set))
  ([conn name]
   (if-let [table (status-lookup (keyword name))]
     (-> common/r
         (.db "rethinkdb")
         (.table table)
         (.run conn)
         (->> (mapv common/to-clj))))))

(defn stats
  ([conn]
   (-> common/r
       (.db "rethinkdb")
       (.table "stats")
       (.run conn))))

(defn logs
  ([conn]
   (-> common/r
       (.db "rethinkdb")
       (.table "logs")
       (.run conn))))


