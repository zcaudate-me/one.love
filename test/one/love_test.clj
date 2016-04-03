(ns one.love-test
	(:use midje.sweet)
  (:require [one.love :refer :all]
            [one.love.raw.connection :as conn]
            [one.love.raw.ast :as ast]
            [one.love.common :as one]))

(def conn (conn/connect conn/+defaults+))

(comment
  (.run (ast/from-clj
         )
        conn)

  (.run (ast/from-clj
         (ast/compile [[:db "test"]
                       [:table "authors"]
                       [:insert [{:name "E.L. James"
                                  :genre "crap"
                                  :country "UK"
                                  :books ["Fifty Shades of Grey"
                                          "Fifty Shades Darker"
                                          "Fifty Shades Freed"]
                                  :tags ["serious" "adult" "spicy"]}]]]))
        conn)

  (-> one/r)

  (count (seq (.run (ast/from-clj
                     (ast/compile [[:table "authors"]]))
                    conn)))

  (first (seq (.run (ast/from-clj
                     (ast/compile [[:table "authors"]]))
                    conn))
         )

)
