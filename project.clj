(defproject im.chit/one.love "0.1.0-SNAPSHOT"
  :description "rethinkdb wrapped in funk"
  :url "https://www.github.com/zcaudate/one.love"
  :license {:name "The MIT License"
            :url "http://http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.rethinkdb/rethinkdb-driver "2.2-beta-5"]
                 [im.chit/vinyasa.maven "0.4.3"]
                 [com.cemerick/pomegranate "0.3.1"]
                 [cheshire "5.5.0"]
                 [im.chit/hara.reflect "2.2.17"]]
  :profiles {:dev {:plugins [[lein-midje "3.1.3"]
                             [lein-hydrox "0.1.16"]]
                   :dependencies [[midje "1.6.3"]
                                  [helpshift/hydrox "0.1.16"]
                                  [cheshire "5.5.0"]]}})
