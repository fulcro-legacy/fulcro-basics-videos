(ns demo.api.mutations
  (:require
    [taoensso.timbre :as timbre]
    [demo.api.read :refer [todo-list-database]]
    [fulcro.server :refer [defmutation]]))

(defmutation ping
  "Server mutation for ping, which just prints incoming parameters to the server log."
  [params]
  (action [env]
    (timbre/info "Deep Thought giggles at your simple parameters: " params)))


(defmutation demo.server-interactions/change-item-label [{:keys [id text] :as params}]
  (action [env]
    (timbre/info "Change label " params)
    (swap! todo-list-database
      assoc-in [:items id :item/label] text)))