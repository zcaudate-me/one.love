(ns one.love.raw.connection
  (:require [one.love.common :as common]))

(defonce +defaults+
  {:host "localhost"
   :port 28015
   :db "test"})

(defn connect
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
