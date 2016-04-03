(ns one.love.raw.command
  (:require [one.love.raw.command
             [func :as func]
             [compile :as compile]]))

(defn prepare [commands]
  (if (compile/command? commands)
    [commands]
    commands))

(defn run
  [[conn db table] commands]
  (-> commands
      prepare
      (cond->
          table (->> (cons [:table table]))
          db    (->> (cons [:db db])))
      vec
      compile/thread
      compile/to-ast
      (.run conn)))
