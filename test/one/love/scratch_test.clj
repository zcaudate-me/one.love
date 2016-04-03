(ns one.love.scratch-test
  (:use midje.sweet)
  (:require [one.love.raw.connection :as conn]
            [one.love.command :as commmand]
            [one.love.command.js :as js]
            [one.love.common :as one])
  (:import com.rethinkdb.gen.ast.Funcall
           com.rethinkdb.gen.ast.Javascript))

(def conn (conn/connect conn/+defaults+))

(comment
  (run [conn]
    [:db-drop "test"])

  (run [conn]
    [:db-create "test"])

  (run [conn]
    [[:db "test"]
     [:table-create "users"]])

  (run [conn]
    [[:db "test"]
     [:table "users"]
     [:insert [{:name "Anne" :age 10 :pets ["dog" "cat"]}
               {:name "Bob"  :age 12 :pets ["dog"]}
               {:name "Chris" :age 10}
               {:name "Dave" :age 11}
               {:name "Edgar" :age 11 :school {:name "Syndal"}}
               {:name "Frank" :age 10}
               {:name "Greg" :age 10}
               {:name "Harry" :age 13}
               {:name "Indy" :age 10}
               {:name "Jact" :age 12}]]])

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:filter {:age 13}]
         [:pluck ["name"]]])
      seq)
  => [{"name" "Harry"}]

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:filter {:age 10 :name "Harry"}]])
      seq)
  => nil

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:filter
          [:and {:age 10} {:name "Harry"}]]
         [:pluck ["name"]]])
      seq)

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:filter '(fn [x] (== x.name "Chris"))]])
      seq)

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:map '(fn [x] (== "Chris" (.getField x "name")))]])
      seq)

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:map '(fn [x] x.name)]])
      seq)

  (js/js '(fn [x] (-> x .-name .-help (= "Chris"))))

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:filter [:javascript "(function(x){return x.name == 'Chris'})"]]])
      seq)

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:filter #(-> % (.getField "name") (= "Chris"))]])
      seq)

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:filter {:school {:name "Syndal"}}]])
      seq)

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:get "39a85584-4245-4997-8695-6f012d17b865"]
         [:get-field "pets"]])
      seq)

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:get "39a85584-4245-4997-8695-6f012d17b865"]
         [:changes]])
      ;;seq
      )

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:get "39a85584-4245-4997-8695-6f012d17b865"]
         [:bracket "pets"]])
      ;;seq
      )
  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:get "39a85584-4245-4997-8695-6f012d17b865"]
         [:get-field "pets"]
         [:nth 1]])
      )

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:get {:name "Anne"}]
         [:get-field "pets"]
         [:nth 1]]))

  (-> one/r (.table "users") (.getAll (to-array []))
      (.run conn))

  (-> one/r
      (.table "users")
      (.filter
       (Javascript. "(function(x){return x.name == 'Chris'})"))
      (.run conn)
      seq)
  #_ #_(.run conn)

  (-> (run [conn]
        [[:db "test"]
         [:table "users"]
         [:order-by "age" "name"
          ;[:eq "Chris"]
          ]])
      seq)

  (run [conn]
    [:javascript "(function(x){return x.name == 'Chris'})(1)"])

  (lambda )


  (compile/to-ast [:row "names"])
  (-> one/r (.row (to-array ["table"])))


  ) 



(comment
  (run [conn] [:le 1 2 3])

  (run [conn] [[:db "test"]
               [:table-create "test"]])

  (run [conn] [:db "test"])

  (run [conn] [[:db "test"]
               [:table "test"]
               [:insert {:data "hello world"}]])

  (run [conn] [[:db "test"]
               [:table "authors"]
               [:insert [{:name "E.L. James"
                          :genre "crap"
                          :country "UK"
                          :books ["Fifty Shades of Grey"
                                  "Fifty Shades Darker"
                                  "Fifty Shades Freed"]
                          :tags ["serious" "adult" "spicy"]}
                         {:name "Stephenie Meyer"
                          :genre "crap"
                          :country "USA"
                          :books ["Twilight" "New Moon" "Eclipse" "Breaking Dawn"]
                          :tags ["weird" "serious"]}]]])
  

  (run [conn "test" "authors"]
    [:delete])

  )

