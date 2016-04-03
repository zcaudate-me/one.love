(ns one.love.raw.system
  (:require [one.love.common :as one]))

(def rethink "rethinkdb")

(defn config
  "interface to get and set values for rethinkdb
       (-> (config conn :server) first keys set)
       => #{:tags :name :cache-size-mb :id}
 
       (-> (config conn :db) first keys set)
       => #{:name :id}
 
       (config conn :cluster)
       => [{:id \"auth\" :auth-key nil}
           {:id \"heartbeat\", :heartbeat-timeout-secs 10}]
 
       (-> (config conn :table) first keys set)
       => #{:db :name :write-acks :durability :id :shards :indexes :primary-key}"
  {:added "0.1"}
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
  "interface for retrieval of statuses
       (-> (status conn :jobs) first keys set)
       => #{:type :id :servers :info :duration-sec}
 
       (-> (status conn :server) first keys set)
       => #{:name :process :id :network}
 
       (-> (status conn :table) first keys set)
       => #{:db :name :status :id :shards :raft-leader}
 
       (status conn :issues)
       => vector?"
  {:added "0.1"}
  ([conn] (-> status-lookup keys set))
  ([conn name]
   (if-let [table (status-lookup (keyword name))]
     (mapv one/to-clj (one/run [conn rethink table])))))

(defn stats
  "interface for retrieval of stats
       (->> (stats conn) first one/to-clj keys set)
       => #{:query-engine :id}"
  {:added "0.1"}
  [conn]
  (one/run [conn rethink "stats" false]))

(defn logs
  "interface for retrieval of logs
       (->> (logs conn) first one/to-clj keys set)
       => #{:server :uptime :level :id :timestamp :message}"
  {:added "0.1"}
  [conn]
  (one/run [conn rethink "logs" false]))
