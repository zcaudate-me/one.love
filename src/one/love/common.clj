(ns one.love.common
  (:require [hara.string.case :as case])
  (:import com.rethinkdb.RethinkDB
           com.rethinkdb.gen.ast.Between))

(defonce r RethinkDB/r)

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

(defn to-clj [result]
  (let [t (type result)]
    (cond (= t java.util.ArrayList)
          (mapv to-clj result)

          (= t java.util.HashMap)
          (reduce (fn [out [k v]]
                    (assoc out (keyword (case/spear-case k)) (to-clj v)))
                  {}
                  result)
          :else result)))

(defn from-clj [result]
  (cond (sequential? result)
        (mapv from-clj result)

        (map? result)
        (reduce (fn [out [k v]]
                  (assoc out (case/camel-case (name k)) (from-clj v)))
                {}
                result)
        :else result))

(defn to-string [result]
  (cond (string? result)
        result

        (keyword? result)
        (.substring (str result) 1)

        :else (str result)))

(defn merge-opts [^Between r opts]
  (reduce (fn [out [k v]]
            (.optArg out (case/camel-case k) v))
          r
          opts))