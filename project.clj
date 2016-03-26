(defproject im.chit/one.love "0.1.0-SNAPSHOT"
  :description "rethinkdb wrapped with funk"
  :url "https://www.github.com/zcaudate/one.love"
  :license {:name "The MIT License"
            :url "http://http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :profiles {:dev {:plugins [[lein-midje "3.1.3"]
                             [lein-hydrox "0.1.16"]]
                   :dependencies [[com.rethinkdb/rethinkdb-driver "2.2-beta-5"]
                                  [midje "1.6.3"]
                                  [helpshift/hydrox "0.1.16"]]}})