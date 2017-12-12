(ns demo.server-interactions
  (:require [devcards.core :as rc :refer-macros [defcard]]
            [fulcro.client.mutations :as m :refer [defmutation]]
            [fulcro.client.dom :as dom]
            [fulcro.events :refer [enter-key?]]
            [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [demo.ui.components :as comp]
            [fulcro.client.data-fetch :as df]))

(defn item-ident [id] [:todo-item/by-id id])
(defn item-path [id field] [:todo-item/by-id id field])

(defn change-item-label* [state-map id text]
  (assoc-in state-map (item-path id :item/label) text))

(defmutation change-item-label [{:keys [id text]}]
  (action [{:keys [state]}]
    (swap! state change-item-label* id text))
  (remote [{:keys [ast]}] true))

(defmutation toggle-complete [{:keys [id]}]
  (action [{:keys [state]}]
    (swap! state update-in (item-path id :item/complete?) not)))

(defsc TodoItem [this {:keys [db/id item/label item/complete? ui/editing?]}]
  {:query [:db/id :item/label :item/complete? :ui/editing?]
   :ident [:todo-item/by-id :db/id]}
  (dom/li nil
    (if editing?
      (dom/input #js {:type      "text"
                      :onChange  (fn [evt] (prim/transact! this
                                             `[(change-item-label {:id ~id :text ~(.. evt -target -value)})]))
                      :onKeyDown (fn [evt]
                                   (if (enter-key? evt)
                                     (m/toggle! this :ui/editing?)))
                      :value     label})
      (dom/span nil
        (dom/input #js {:type    "checkbox"
                        :onClick (fn [evt] (prim/transact! this `[(toggle-complete {:id ~id})]))
                        :checked complete?})
        (dom/a #js {:onDoubleClick (fn [] (m/toggle! this :ui/editing?))} label)))))

(def ui-todo-item (prim/factory TodoItem {:keyfn :db/id}))

(defsc TodoList [this {:keys [db/id list/name list/items] :as props}]
  {:query [:db/id :list/name {:list/items (prim/get-query TodoItem)}]
   :ident [:todo-list/by-id :db/id]}
  (dom/div nil
    (dom/h4 nil name)
    (mapv ui-todo-item items)))

(def ui-todo-list (prim/factory TodoList))

(defsc Root [this {:keys [ui/react-key root/todo-list]}]
  {:query         [:ui/react-key {:root/todo-list (prim/get-query TodoList)}]
   :initial-state (fn [params] {})}
  (dom/div #js {:key react-key}
    (ui-todo-list todo-list)))

(defcard-fulcro server-interactions
  Root
  {}
  {:inspect-data true
   :fulcro       {:started-callback (fn [app]
                                      (df/load app :todo-list TodoList {:marker false
                                                                        :target [:root/todo-list]}))}})
