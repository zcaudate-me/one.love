(ns one.love.command-test
  (:use midje.sweet)
  (:require [one.love.command :refer :all]
            [one.love.raw.connection :as conn]))


(def conn (conn/connect conn/+defaults+))

^{:refer one.love.command/command? :added "0.1"}
(fact "checks to see if the form is a command"
  (command? 1) => false
  (command? [:insert]) => true)

^{:refer one.love.command/to-ast-args :added "0.1"}
(fact "sorts the input vector into arguments and optargs"
  (to-ast-args [1 2 3])
  => [[1 2 3] {}]

  (to-ast-args [1 2 3 :opts {:hello :world}])
  => [[1 2 3] {:hello :world}])

^{:refer one.love.command/to-ast :added "0.1"}
(fact "creates a clojure datastructure to a rethinkdb ast"
  (with-out-str
    (pr (to-ast [:le 1 2 4])))
  => "#ast[:le 1 2 4]"

  (.run (to-ast [:le 1 2 4])
        conn)
  => true)

^{:refer one.love.command/create-key :added "0.1"}
(fact "creates a key from the class"
  (create-key com.rethinkdb.gen.ast.Le)
  => :le

  (create-key com.rethinkdb.gen.ast.Datum)
  => :datum)

^{:refer one.love.command/get-arguments :added "0.1"}
(fact "get arguments from rethinkdb ast"
  (get-arguments (to-ast [:le 1 2 3 4]))
  => [1 2 3 4])

^{:refer one.love.command/get-optargs :added "0.1"}
(fact "get optargs from rethinkdb ast"
  (get-optargs (to-ast [:make-obj :opts {:hello "world"}]))
  => {:hello "world"})

^{:refer one.love.command/to-data :added "0.1"}
(fact "converts a rethinkdb ast to a clojure datastructure"
  (-> (to-ast [:le 1 2 3 4])
      (to-data))
  => [:le 1 2 3 4])

^{:refer one.love.command/thread :added "0.1"}
(fact "threads forms so that it can be converted to ast"
  (thread [[:db "test"]
           [:table "hello"]
           [:insert {:data "hello world"}]])
  => [:insert [:table [:db "test"] "hello"] {:data "hello world"}])

^{:refer one.love.command/unthread :added "0.1"}
(fact "restores a threaded ast to a more readable form"
  (unthread [:insert [:table [:db "test"] "hello"] {:data "hello world"}])
  => [[:db "test"]
      [:table "hello"]
      [:insert {:data "hello world"}]])
 
