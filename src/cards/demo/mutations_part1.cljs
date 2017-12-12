(ns demo.mutations-part1
  (:require [devcards.core :as rc :refer-macros [defcard]]
            [fulcro.client.mutations :refer [defmutation]]
            [fulcro.client.dom :as dom]
            [fulcro.events :refer [enter-key?]]
            [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [demo.ui.components :as comp]))


(defmutation change-item-label [{:keys [text]}]
  (action [{:keys [state]}]
    (swap! state assoc :item/label text)))

(defmutation toggle-complete [{:keys [text]}]
  (action [{:keys [state]}]
    (swap! state update :item/complete? not)))

(defmutation finish-editing [ignored]
  (action [{:keys [state]}]
    (swap! state assoc :ui/editing? false)))

(defsc TodoItem [this {:keys [db/id item/label item/complete? ui/editing?]}]
  {:initial-state {:db/id       1 :item/label "Buy stuff" :item/complete? false
                   :ui/editing? true}}
  (dom/li nil
    (if editing?
      (dom/input #js {:type      "text"
                      :onChange  (fn [evt] (prim/transact! this `[(change-item-label {:text ~(.. evt -target -value)})]))
                      :onKeyDown (fn [evt]
                                   (if (enter-key? evt)
                                     (prim/transact! this `[(finish-editing {})])))
                      :value     label})
      (dom/span nil
        (dom/input #js {:type    "checkbox"
                        :onClick (fn [evt] (prim/transact! this `[(toggle-complete {})]))
                        :checked complete?})
        label))))

(defcard-fulcro active-todo-item-1
  TodoItem
  {}
  {:inspect-data true})