(ns one.love.raw.command.func-test
  (:use midje.sweet)
  (:require [one.love.raw.command.func :refer :all]
            [one.love.raw.connection :as conn]))

(def conn (conn/connect conn/+defaults+))

^{:refer one.love.raw.command.func/rethink-ast :added "0.1"}
(fact "reads all ast-classes from the `com.rethinkdb.gen.ast` package")

^{:refer one.love.raw.command.func/create-term-form :added "0.1"}
(fact "creates a term form from a class name")

^{:refer one.love.raw.command.func/create-term-fn :added "0.1"}
(fact "create a term function from a class name")
