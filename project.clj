(defproject im.chit/one.love "0.1.4"
  :description "rethinkdb wrapped in funk"
  :url "https://www.github.com/zcaudate/one.love"
  :license {:name "The MIT License"
            :url "http://http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.rethinkdb/rethinkdb-driver "2.3.2"]
                 [im.chit/hara.reflect "2.3.7"]
                 [im.chit/hara.string.case "2.3.7"]]
  :profiles {:dev {:plugins [[lein-midje "3.1.3"]
                             ;[lein-hydrox "0.1.16"]
                             ]
                   :source-paths ["src" "dev"]
                   :dependencies [[midje "1.6.3"]
                                  ;[helpshift/hydrox "0.1.16"]
                                  ;[cheshire "5.5.0"]
                                  ]}})
