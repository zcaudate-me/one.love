(ns gen-ast-list
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(def +rethink-snippet+ #"rethinkdb/rethinkdb-driver")
(def +java-class-path+ "java.class.path")

(defn generate []
  (let [paths   (-> (System/getProperties)
                    (get +java-class-path+)
                    (string/split #":"))
        path (->> paths
                  (filter (fn [path]
                            (re-find +rethink-snippet+ path)))
                  first)
        jar  (java.util.jar.JarFile. path)]
    (->> (.entries jar)
         (iterator-seq)
         (map #(.getName %))
         (keep #(when-let [matches (re-find #"(com/rethinkdb/gen/ast/(\w+)).class" %)]
                  (nth matches 2)))
         (sort)
         (pr-str)
         (spit "resources/rethinkdb-ast.edn"))))

