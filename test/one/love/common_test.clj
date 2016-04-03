(ns one.love.common-test
  (:use midje.sweet)
  (:require [one.love.common :refer :all]))

^{:refer one.love.common/to-clj :added "0.1"}
(fact "converts a rethinkdb result to a clojure datastructure"
  (to-clj (java.util.ArrayList.
           [(java.util.HashMap. {"id" "hello" "newKey" true})
            (java.util.HashMap. {"id" "hello" "newKey" false})]))
  => [{:new-key true, :id "hello"} {:new-key false, :id "hello"}])

^{:refer one.love.common/from-clj :added "0.1"}
(fact "converts a clojure datastructure into a rethinkdb compatible input:"
  (from-clj {:foo-bar "baz"})
  => {"fooBar" "baz"})

^{:refer one.love.common/to-string :added "0.1"}
(fact "converts keyword/string to string"
  (to-string :hello) => "hello"
  (to-string "hello") => "hello")
