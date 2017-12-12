(ns demo.api.read
  (:require
    [fulcro.server :refer [defquery-entity defquery-root]]
    [taoensso.timbre :as timbre]
    [fulcro.client.primitives :as prim]))

(defquery-entity :meaning/by-id
  "Returns the meaning of life."
  (value [{:keys [query]} id params]
    (let [meanings {:life       42
                    :universe   42
                    :everything 42}]
      (timbre/info "Thinking about the meaning of " query "...hmmm...")
      (Thread/sleep 3000)
      (select-keys meanings query))))


(def todo-list-database (atom
                          {:items {4 {:db/id 4 :item/label "X" :item/complete? true}
                                   5 {:db/id 5 :item/label "Y" :item/complete? false}}
                           :lists {1
                                   {:db/id      1 :list/name "My List"
                                    :list/items [[:items 4] [:items 5]]}}}))

(defquery-root :todo-list
  (value [{:keys [query]} {:keys [id]}]
    (let [data   (prim/db->tree [{[:lists 1] query}] @todo-list-database @todo-list-database)
          result (get data [:lists 1])]
      result)))