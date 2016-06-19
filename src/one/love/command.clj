(ns one.love.command
  (:require [one.love.command.ast :as ast]
            [one.love.command.js :as js]
            [one.love.command.lambda :as lambda]
            [clojure.walk :as walk]
            [clojure.string :as string]
            [hara.reflect :as reflect]
            [hara.string.case :as case])
  (:import com.rethinkdb.ast.ReqlAst
           [com.rethinkdb.gen.ast Datum MakeArray MakeObj Func Funcall]))

(defn ast?
  [x]
  (instance? ReqlAst x))

(defn command?
  "checks to see if the form is a command
       (command? 1) => false
       (command? [:insert]) => true"
  {:added "0.1"} [form]
  (and (vector? form)
       (keyword? (first form))))

(defn thread?
  {:added "0.1"}
  [form]
  (and (vector? form)
       (command? (first form))))

(defn js-function?
  [form]
  (and (list? form)
       (= (first form) 'fn)))

(defn lambda-function?
  [form]
  (and (list? form)
       (vector? (first form))))

(defn thread
  "threads forms so that it can be converted to ast
       (thread [[:db \"test\"]
                [:table \"hello\"]
                [:insert {:data \"hello world\"}]])
       => [:insert [:table [:db \"test\"] \"hello\"] {:data \"hello world\"}]"
  {:added "0.1"} [[f & forms]]
  (loop [f f forms forms]
    (cond (empty? forms)
          f

          :else
          (let [[[k & args] & more] forms]
            (recur (apply vector k f args) more)))))

(defn unthread
  "restores a threaded ast to a more readable form
       (unthread [:insert [:table [:db \"test\"] \"hello\"] {:data \"hello world\"}])
       => [[:db \"test\"]
           [:table \"hello\"]
           [:insert {:data \"hello world\"}]]"
  {:added "0.1"}
  [form]
  (loop [[k f? & more :as form] form
         out ()]
    (if (command? f?)
      (recur f? (cons (apply vector k more) out))
      (vec (cons form out)))))

(defn to-ast-args
  "sorts the input vector into arguments and optargs
       (to-ast-args [1 2 3])
       => [[1 2 3] {}]
 
       (to-ast-args [1 2 3 :opts {:hello :world}])
       => [[1 2 3] {:hello :world}]"
  {:added "0.1"} [args]
  (let [[opts? k? & rest] (reverse args)]
    (if (and (= k? :opts)
             (map? opts?))
      [(-> rest reverse vec) opts?]
      [(vec args) {}])))

(defn to-ast
  "creates a clojure datastructure to a rethinkdb ast
       (with-out-str
         (pr (to-ast [:le 1 2 4])))
       => \"#ast[:le 1 2 4]\"
 
       (.run (to-ast [:le 1 2 4])
             conn)
       => true"
  {:added "0.1"}
  [form]
  (cond (command? form)
        (let [[k & args] form
              [args opts] (to-ast-args args)
              func (or (ast/fns k)
                       (throw (Exception. (str "Invalid term: " k))))]
          (func (mapv to-ast args) opts))

        (thread? form)
        (to-ast (thread form))

        (ast? form)
        form

        (js-function? form)
        (to-ast [:javascript
                 (str "(" (js/js form) ")")])

        (lambda-function? form)
        (lambda/compile-lambda form)

        :else
        form))

(defn create-key
  "creates a key from the class
       (create-key com.rethinkdb.gen.ast.Le)
       => :le
 
       (create-key com.rethinkdb.gen.ast.Datum)
       => :datum"
  {:added "0.1"} [cls]
  (-> (.getName cls)
      (string/split #"\.")
      last
      case/spear-case
      keyword))

(declare to-data)

(defn get-arguments
  "get arguments from rethinkdb ast
       (get-arguments (to-ast [:le 1 2 3 4]))
       => [1 2 3 4]"
  {:added "0.1"} [ast]
  (->> (reflect/apply-element ast "args" [])
       (mapv to-data)))

(defn get-optargs
  "get optargs from rethinkdb ast
       (get-optargs (to-ast [:make-obj :opts {:hello \"world\"}]))
       => {:hello \"world\"}"
  {:added "0.1"} [ast]
  (->> (reflect/apply-element ast "optargs" [])
       (reduce (fn [out [k v]]
                 (assoc out (keyword k) (to-data v)))
               {})))

(defn to-data
  "converts a rethinkdb ast to a clojure datastructure
       (-> (to-ast [:le 1 2 3 4])
           (to-data))
       => [:le 1 2 3 4]"
  {:added "0.1"} [ast]
  (cond (= Datum (type ast))
        (reflect/apply-element ast "datum" [])

        (= MakeArray (type ast))
        (get-arguments ast)

        (= MakeObj (type ast))
        (get-optargs ast)

        (instance? ReqlAst ast)
        (let [k (create-key (type ast))
              args (get-arguments ast)
              opts (get-optargs ast)]
          (->> (cond-> '()
                 (-> opts empty? not) (->> (cons opts) (cons :opts))
                 (-> args empty? not) (->> (concat args)))
               (cons k)
               vec))

        :else ast))

(defmethod print-method ReqlAst
  [v w]
  (.write w (str "#ast" (to-data v))))

(defmethod print-method Datum
  [v w]
  (.write w (str "#datum" (to-data v))))

(defn prepare [commands]
  (if (command? commands)
    [commands]
    commands))

