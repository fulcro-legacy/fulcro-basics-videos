(ns demo.intro
  (:require [devcards.core :as rc :refer-macros [defcard]]
            [fulcro.client.dom :as dom]
            [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [demo.ui.components :as comp]))

(defsc TodoItem [this {:keys [db/id item/label item/complete? ui/editing?]}]
  (dom/li nil

    (if editing?
      (dom/input #js {:type  "text"
                      :value label})
      (dom/span nil
        (dom/input #js {:type    "checkbox"
                        :checked complete?})
        label))))

(def ui-todo-item (prim/factory TodoItem))

(defcard todo-item-unchecked
  "A TODO Item"
  (ui-todo-item {:db/id 1 :item/label "Buy Milk" :item/complete? false :ui/editing? false}))

(defcard todo-item-checked
  "A TODO Item"
  (ui-todo-item {:db/id 1 :item/label "Buy Milk" :item/complete? true :ui/editing? false}))

(defcard todo-item-edit-in-progress
  "A TODO Item"
  (ui-todo-item {:db/id 1 :item/label "Buy Milk" :item/complete? true :ui/editing? true}))

(defcard todo-item-edit-in-progress
  "A TODO Item"
  (ui-todo-item {:db/id 1 :item/label "Buy Milk" :item/complete? false :ui/editing? true}))

#_(defsc Root [this {:keys [name] :as props}]
    {:initial-state {:name "Tony"}}
    (dom/div #js {}
      (str "Hi " name)))

#_(defcard-fulcro my-card
    Root
    {}
    {:inspect-data true})


