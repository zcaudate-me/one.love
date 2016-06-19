(ns one.love.command.lambda
  (:import [com.rethinkdb.gen.ast
            ReqlFunction1
            ReqlFunction2
            ReqlFunction3
            ReqlFunction4]))

(defn compile-lambda
  [[args & body :as form]]
  (let [cls (or (get {1 'com.rethinkdb.gen.ast.ReqlFunction1 
                      2 'com.rethinkdb.gen.ast.ReqlFunction2
                      3 'com.rethinkdb.gen.ast.ReqlFunction3 
                      4 'com.rethinkdb.gen.ast.ReqlFunction4}
                     (count args))
                (throw (Exception. (str "Wrong number of arguments: " args))))]
    (eval (list `reify cls
                (apply list `apply (apply vector 'this args)
                       body)))))

