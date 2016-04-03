(ns one.love.raw.connection
  (:require [one.love.common :as common]))

(defonce +defaults+
  {:host "localhost"
   :port 28015
   :db "test"})

(defn connect
  "creates a connection to rethinkdb
       (-> (connect +defaults+)
           (.isOpen))
       => true
 
       (-> (doto (connect +defaults+)
             (.close))
           (.isOpen))
       => false"
  {:added "0.1"}
  [{:keys [host port auth timeout cert ssl db recreate?] :as m}]
  (let [conn (-> common/r
                 (.connection)
                 (.hostname host)
                 (.port port))
        conn (cond-> conn
               db      (.db db)
               auth    (.authKey auth)
               timeout (.timeout timeout)
               cert    (.certFile cert)
               ssl     (.sslContext ssl))]
    (.connect conn)))
