(ns one.love.raw.connection-test
  (:use midje.sweet)
  (:require [one.love.raw.connection :refer :all]))

^{:refer one.love.raw.connection/connect :added "0.1"}
(fact "creates a connection to rethinkdb"
  (-> (connect +defaults+)
      (.isOpen))
  => true

  (-> (doto (connect +defaults+)
        (.close))
      (.isOpen))
  => false)
 
