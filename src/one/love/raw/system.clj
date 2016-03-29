(ns one.love.raw.system
  (:require [one.love.common :as one]))

(def rethink "rethinkdb")

(defn config
  ([conn] #{:table :server :db :cluster})
  ([conn name]
   (let [name (str (one/to-string name) "_config")]
     (mapv one/to-clj  (one/run [conn rethink name]))))
  ([conn name query update]
   (let [name (str (one/to-string name) "_config")]
     (one/run [conn rethink name]
       (.update update)))))

(def status-lookup
  {:table "table_status"
   :server "server_status"
   :issues "current_issues"
   :jobs   "jobs"})

(defn status
  ([conn] (-> status-lookup keys set))
  ([conn name]
   (if-let [table (status-lookup (keyword name))]
     (mapv one/to-clj (one/run [conn rethink table])))))

(defn stats
  [conn]
  (one/run [conn rethink "stats" false]))

(defn logs
  [conn]
  (one/run [conn rethink "logs" false]))
