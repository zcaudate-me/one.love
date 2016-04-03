(ns one.love.command.ast
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [hara.string.case :as case]
            [clojure.walk :as walk])
  (:import java.util.jar.JarFile)
  (:refer-clojure :exclude [>]))

(def +rethink-snippet+ #"rethinkdb/rethinkdb-driver")
(def +java-class-path+ "java.class.path")

(defn rethink-ast
  "reads all ast-classes from the `com.rethinkdb.gen.ast` package"
  {:added "0.1"} []
  (let [paths   (-> (System/getProperties)
                    (get +java-class-path+)
                    (string/split #":"))
        path (->> paths
                  (filter (fn [path]
                            (re-find +rethink-snippet+ path)))
                  first)
        jar  (java.util.jar.JarFile. path)
        ast  (->> (.entries jar)
                  (iterator-seq)
                  (map #(.getName %))
                  (keep #(re-find #"(com/rethinkdb/gen/ast/(\w+)).class" %))
                  (map (fn [[_ path cls]]
                         [(-> cls case/spear-case keyword)
                          (Class/forName (.replaceAll path "/" "."))]))
                  (into {}))]
    ast))

(defmacro create-term-form
  "creates a term form from a class name"
  {:added "0.1"}
  [name cls]
  `(defn ~name
     ([] (~name [] {}))
     ([~'args] (~name ~'args {}))
     ([~'args ~'opts]
      (new ~cls
           (com.rethinkdb.model.Arguments. (walk/stringify-keys ~'args))
           (com.rethinkdb.model.OptArgs/fromMap ~'opts)))))

(defn create-term-fn
  "create a term function from a class name"
  {:added "0.1"} [k cls]
  (eval (list `create-term-form
              (->> (name k)
                   (str "ast-")
                   symbol)
              cls)))

(defonce classes
  (dissoc (rethink-ast)
          :reql-function0
          :reql-function1
          :reql-function2
          :reql-function3
          :reql-function4
          :reql-expr
          :reql-ast
          :datum
          :func))

(defonce fns (reduce-kv (fn [out k v]
                          (assoc out k (create-term-fn k v)))
                        {}
                        classes))
