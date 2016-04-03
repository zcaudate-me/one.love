(ns one.love
  (:require [one.love.classic :as classic]
            [one.love.common :as common]
            [one.love.raw.connection :as conn]
            [one.love.raw.command :as command]))

(comment
  (connect! {:host "localhost"
             :port 28015
             :schema {:post {:author [{:type :ref
                                       :ref {:ns :author}}]
                             :tags   [{:type :ref
                                       :ref {:ns :tag}}]}
                      :author {:name     [{}]
                               :location [{:type :ref
                                           :ref {:ns :location}}]}
                      :tag {:name [{}]}
                      :location {:name [{}]
                                 :neighbour [{:type :geo}]}}

             :tables #{:post :location}

             :embed  {:post #{:author :tags}
                      :author #{:location}}

             :index  {:post {:author_name [:author :name]}}}))
