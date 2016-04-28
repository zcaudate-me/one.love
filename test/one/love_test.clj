(ns one.love-test
	(:use midje.sweet)
  (:require [one.love :refer :all]
            [one.love.raw.connection :as conn]
            [one.love.common :as one]
            [one.love.command :as command])
  (:import com.rethinkdb.gen.ast.Func))

(def conn (connect))

(fact
  (run conn [:db-drop "test"])
  => (contains {:dbs-dropped 1
                :tables-dropped 0,
                :config-changes vector?})

  (run conn
    [:db-create "test"])
  => (contains {:dbs-created 1
                :config-changes vector?})

  (run conn
    [:db-list])
  => ["rethinkdb" "test"]

  (run conn
    [[:db "test"]
     [:table-create "movies"]])
  => (contains {:tables-created 1,
                :config-changes vector?})

  (def results
    (run conn
      [[:table "movies"]
       [:insert [:http "http://rethinkdb.com/sample/top-250-ratings.json"]]]))

  (:inserted results)
  => 253

  (run conn
    [[:table "movies"]
     [:without "id"]
     [:filter {:rank 1}]])
  => [{:title "The Shawshank Redemption"
        :year 1994,
        :rating 9.2,
        :rank 1,
        :votes 1262930,}]

  (run conn
    [[:table "movies"]
     [:without "id"]
     [:distinct]
     [:count]])
  => 250
  

  (run conn
    [[:table "movies"]
     [:without "id"]
     [:distinct]])

  (doto conn
    (run [:table-create "unique_movies"])
    (run [[:table "unique_movies"]
          [:insert [[:table "movies"]
                    [:without "id"]
                    [:distinct]]]]))

  (run conn
    [[:table "unique_movies"]
     [:order-by "rank"]
     [:limit 10]
     [:map '(fn [movie] movie.title)]])
  => ["The Shawshank Redemption"
      "The Godfather"
      "The Godfather: Part II"
      "The Dark Knight"
      "Pulp Fiction"
      "Il buono, il brutto, il cattivo."
      "Schindler's List"
      "12 Angry Men"
      "The Lord of the Rings: The Return of the King"
      "Fight Club"]

  (run conn
    [[:table "unique_movies"]
     [:order-by [:desc "rank"]]
     [:limit 10]
     [:pluck ["year" "title"]]])
  => [{:year 1995, :title "Underground"}
      {:year 2006, :title "Blood Diamond"}
      {:year 2003, :title "Bom yeoreum gaeul gyeoul geurigo bom"}
      {:year 1973, :title "Papillon"}
      {:year 2010, :title "Tropa de Elite 2 - O Inimigo Agora ... Outro"}
      {:year 2004, :title "Before Sunset"}
      {:year 1956, :title "The Killing"}
      {:year 1948, :title "Rope"}
      {:year 1991, :title "Beauty and the Beast"}
      {:year 1983, :title "A Christmas Story"}]
  

  (run conn
    [[:table "unique_movies"]
     [:filter '(fn [x]
                 (or (= x.rank 1)
                     (= x.rank 2)
                     (= x.rank 6)))]
     [:without "id"]
     [:order-by "rank"]])
  => [{:year 1994, :rating 9.2, :rank 1, :votes 1262930, :title "The Shawshank Redemption"}
      {:year 1972, :rating 9.2, :rank 2, :votes 872079, :title "The Godfather"}
      {:year 1966, :rating 8.9, :rank 6, :votes 378441, :title "Il buono, il brutto, il cattivo."}]

  (run conn
    [[:table "unique_movies"]
     [:order-by "rank"]
     [:limit 25]
     [:avg "votes"]])
  => 706029.08

  (run conn
    [[:table "unique_movies"]
     [:order-by "rank"]
     [:limit 25]
     [:max "year"]
     [:without "id"]])
  => {:year 2010, :rating 8.7, :rank 13, :votes 1010644, :title "Inception"}

  (run conn
    [[:table "unique_movies"]
     [:filter '(fn [x] (< x.votes 100000))]
     [:min "rank"]
     [:without "id"]])
  => {:year 1931, :rating 8.5, :rank 35, :votes 75880, :title "City Lights"})
