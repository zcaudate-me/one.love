(ns one.love.raw.system-test
  (:use midje.sweet)
  (:require [one.love.raw
             [connection :as conn]
             [system :refer :all]]
            [one.love.common :as common]))

(def conn (conn/connect conn/+defaults+))

^{:refer one.love.raw.system/config :added "0.1"}
(fact "interface to get and set values for rethinkdb"

      (-> (config conn :server) first keys set)
      => #{:tags :name :cache-size-mb :id}

      (-> (config conn :db) first keys set)
      => #{:name :id}

      (config conn :cluster)
      => [{:id "auth" :auth-key nil}
          {:id "heartbeat", :heartbeat-timeout-secs 10}]

      (-> (config conn :table) first keys set)
      => #{:db :name :write-acks :durability :id :shards :indexes :primary-key})


^{:refer one.love.raw.system/status :added "0.1"}
(fact "interface for retrieval of statuses"
      (-> (status conn :jobs) first keys set)
      => #{:type :id :servers :info :duration-sec}

      (-> (status conn :server) first keys set)
      => #{:name :process :id :network}

      (-> (status conn :table) first keys set)
      => #{:db :name :status :id :shards :raft-leader}

      (status conn :issues)
      => vector?)

^{:refer one.love.raw.system/stats :added "0.1"}
(fact "interface for retrieval of stats"
      (->> (stats conn) first common/to-clj keys set)
      => #{:query-engine :id})

^{:refer one.love.raw.system/logs :added "0.1"}
(fact "interface for retrieval of logs"
      (->> (logs conn) first common/to-clj keys set)
      => #{:server :uptime :level :id :timestamp :message})
