(ns one.love.common
  (:require [hara.string.case :as case])
  (:import com.rethinkdb.RethinkDB
           com.rethinkdb.gen.ast.Between))

(def r RethinkDB/r)

(defrecord Rethink []
  Object
  (toString [{:keys [conn] :as rt}]
    (str "#rethink" (-> (into {} rt)
                        (assoc :conn (if (.isOpen conn)
                                       :alive
                                       :dead))))))

(defmethod print-method Rethink
  [v w]
  (.write w (str v)))

(defn to-clj
  "converts a rethinkdb result to a clojure datastructure
       (to-clj (java.util.ArrayList.
                [(java.util.HashMap. {\"id\" \"hello\" \"newKey\" true})
                 (java.util.HashMap. {\"id\" \"hello\" \"newKey\" false})]))
       => [{:new-key true, :id \"hello\"} {:new-key false, :id \"hello\"}]"
  {:added "0.1"} [result]
  (let [t (type result)]
    (cond (= t java.util.ArrayList)
          (mapv to-clj result)

          (= t java.util.HashMap)
          (reduce (fn [out [k v]]
                    (assoc out (keyword (case/spear-case k)) (to-clj v)))
                  {}
                  result)
          :else result)))

(defn from-clj
  "converts a clojure datastructure into a rethinkdb compatible input:
       (from-clj {:foo-bar \"baz\"})
       => {\"fooBar\" \"baz\"}"
  {:added "0.1"} [result]
  (cond (sequential? result)
        (mapv from-clj result)

        (map? result)
        (reduce (fn [out [k v]]
                  (assoc out (case/camel-case (name k)) (from-clj v)))
                {}
                result)
        :else result))

(defn to-string
  "converts keyword/string to string
       (to-string :hello) => \"hello\"
       (to-string \"hello\") => \"hello\""
  {:added "0.1"} [result]
  (cond (string? result)
        result

        (keyword? result)
        (.substring (str result) 1)

        :else (str result)))

(defmacro run [[conn db table clj] & body]
  `(-> RethinkDB/r
       ~@(if db [`(.db ~db)])
       ~@(if table [`(.table ~table)])
       ~@body
       (.run ~conn)
       ~@(if (false? clj) [] [`to-clj])))

(defmacro do-> [obj & body]
  (let [[prev end] [(butlast body) (last body)]]
    `(-> (doto ~obj
           ~@prev)
         ~end)))

(defn merge-opts
  ([^Between r opts]
   (merge-opts r opts case/camel-case))
  ([^Between r opts key-fn]
   (reduce (fn [out [k v]]
             (.optArg out (key-fn k) v))
           r
           opts)))


