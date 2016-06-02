# one.love

[![Build Status](https://travis-ci.org/zcaudate/one.love.png?branch=master)](https://travis-ci.org/zcaudate/one.love)

rethinkdb wrapped in funk

![](https://raw.githubusercontent.com/zcaudate/one.love/master/bob-marley.jpg)

### Installation

Add to project.clj dependencies:

```clojure
[im.chit/one.love "0.1.2"]
```

## Usage

```clojure
(require '[one.love :refer :all])
```

## Documentation

TBD


## Walkthrough

This walkthrough follows the data exploration [guide](http://www.rethinkdb.com/docs/introduction-to-reql/) on the rethinkdb [website](http://www.rethinkdb.com)

## Connection

Creating a connection:

```clojure
(def conn (connect {:host "localhost"  ; defaults
                    :port 28015}))
(println conn)
;;=> #rethink{:host "localhost", :port 28015, :db "test", :conn :alive}
```

#### Creating Data

Akshay Chougule, a biologist working with large data sets, wrote a blog post about [Useful Unix commands for exploring data](http://datavu.blogspot.com/2014/08/useful-unix-commands-for-exploring-data.html), showing ways to “query” comma-delimited plain text data sets with common shell commands.

We’ll use data from the IMDb Top 250. (Note that we captured this on August 26, 2014, so the data will likely be different if you check it now.) The plain-text data from IMDb isn’t in any format, but we’ve turned it into a JSON file available at http://rethinkdb.com/sample/top-250-ratings.json. (For the record, we converted it to a tab-delimited file first, used rethinkdb import to get it into a database, fixed the column types and re-exported it. See Importing your data for details.)

Since it’s available online, you can import the sample data set just by creating a table:

```clojure
(run conn
    [[:db "test"]
     [:table-create "movies"]])
```

And then importing it over http:

```clojure
(run conn
      [[:table "movies"]
       [:insert [:http "http://rethinkdb.com/sample/top-250-ratings.json"]]])
```

The table created has six fields: an automatically generated primary key (id), rank (the IMDb rank, 1–250), rating (on a 1–10 scale), title, votes, and year.

#### Get the top movie

So we can see IMDb’s number one movie with filter({rank: 1}):

```clojure
(run conn
  [[:table "movies"]
   [:without "id"]
   [:filter {:rank 1}]])
=> [{:title "The Shawshank Redemption"
    :year 1994,
    :rating 9.2,
    :rank 1,
    :votes 1262930,}]
```

#### Removing duplicate documents

You might have caught that there were 253 documents inserted, not 250. Either we have the top 253 movies, or there are a few duplicate records lurking in there. We can use distinct to get a count of unique rows, but we need to remove the id column from the query, since all ID values are unique.

```clojure
(run conn
  [[:table "movies"]
   [:without "id"]
   [:distinct]
   [:count]])
=> 250
```

To get the list without duplicates, we can simply leave off `:count`.

```clojure
(run conn
    [[:table "movies"]
     [:without "id"]
     [:distinct]])
;=> [.... results ....]
```

To put these into a new table, wrap that query with insert. We’ll get new IDs generated automatically. This is also an example of using subqueries with ReQL: it’s easy to pass the results of one query into another. (One of ReQL’s other nice properties, which we’ve already seen, is command chaining: the input of one command is often the output of the command before it, similar to Unix piping.)


```clojure
(doto conn
    (run [:table-create "unique_movies"])
    (run [[:table "unique_movies"]
          [:insert [[:table "movies"]
                    [:without "id"]
                    [:distinct]]]]))
```

Now with a “clean” data set we can run simple commands in the repl (you can also add .without('id') in the command chain to “prettify” the output:

#### Display the top 10 movies

```clojure
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

```

#### Display the bottom 10 movies

```clojure
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
```

(Those are the bottom 10 of the top 250, so they’re still pretty good.)


#### Get the 1st, 2nd ,6th records

```clojure
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

```

#### Find the average number of votes for the top 25 movies

```clojure
(run conn
  [[:table "unique_movies"]
   [:order-by "rank"]
   [:limit 25]
   [:avg "votes"]])
=> 706029.08
```

#### Find the most recent movie in the top 25

```clojure
(run conn
  [[:table "unique_movies"]
   [:order-by "rank"]
   [:limit 25]
   [:max "year"]
   [:without "id"]])
=> {:year 2010, :rating 8.7, :rank 13, :votes 1010644, :title "Inception"}
```

#### Find the highest-ranked movie with under 100,000 votes

```clojure
(run conn
    [[:table "unique_movies"]
     [:filter '(fn [x] (< x.votes 100000))]
     [:min "rank"]
     [:without "id"]])
=> {:year 1931, :rating 8.5, :rank 35, :votes 75880, :title "City Lights"})
```

You can read more about ReQL in the [Introduction to ReQL](http://www.rethinkdb.com/docs/introduction-to-reql/) article, or go into greater depth with the [API documentation](http://www.rethinkdb.com/api/javascript/).

```clojure
```


## License

Copyright © 2016 Chris Zheng

Distributed under the MIT License
