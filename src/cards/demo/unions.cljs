(ns demo.unions
  (:require [devcards.core :as rc :refer-macros [defcard]]
            [fulcro.client.mutations :as m :refer [defmutation]]
            [fulcro.client.dom :as dom]
            [fulcro.events :refer [enter-key?]]
            [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [demo.ui.components :as comp]))

(defn stream-item-ident
  "Calculate the ident of an incoming arbitrary set of props that could be a comment or an image."
  [{:keys [db/id image/url comment/text]}]
  (if url
    [:images/by-id id]
    [:comments/by-id id]))

(defn make-image [id url]
  {:db/id     id
   :image/url url})

(defn make-comment [id text]
  {:db/id        id
   :comment/text text})

;; In the video these had initial state. That was the source of the extra `nil` data in the app state. You see, if Fulcro
;; sees union targets that have initial state, it will try to make sure they end up in state; however, for that to be
;; clean the union *must* also have initial state to "choose" which is the "default". Again, this is really only
;; of much use on unions as UI routers. The other kinds are loaded from servers and would never have initial state.
;; To make this example clean, we've defined builder functions above so we don't use initial state
(defsc Image [this {:keys [db/id image/url] :as props}]
  {:query [:db/id :image/url]
   :ident (fn [] (stream-item-ident props))}
  (dom/div nil (str "I am an Image " url)))

(defsc Comment [this {:keys [db/id comment/text] :as props}]
  {:query [:db/id :comment/text]
   :ident (fn [] (stream-item-ident props))}
  (dom/div nil (str "Comment: " text)))

(def ui-image (prim/factory Image {:keyfn :db/id}))
(def ui-comment (prim/factory Comment {:keyfn :db/id}))

(defsc StreamItemUnion
  [this props]
  {:query (fn [] {:comments/by-id (prim/get-query Comment)
                  :images/by-id   (prim/get-query Image)})
   :ident (fn [] (stream-item-ident props))}
  (let [kind (if (contains? props :image/url) :image :comment)]
    (dom/li nil
      (case kind
        :image (ui-image props)
        :comment (ui-comment props)))))

(def ui-stream-item (prim/factory StreamItemUnion {:keyfn :db/id}))

(defmutation route-to-2 [params]
  (action [{:keys [state]}]
    (swap! state assoc :root/post [:images/by-id 2])))

(defmutation route-to-8 [params]
  (action [{:keys [state]}]
    (swap! state assoc :root/post [:comments/by-id 8])))

(defsc Root [this {:keys [ui/react-key root/post]}]
  {:query         [:ui/react-key
                   ; the real thing(s)
                   {:root/post (prim/get-query StreamItemUnion)}
                   ; a way to put extra stuff in state
                   {:root/fake-key (prim/get-query StreamItemUnion)}]
   :initial-state (fn [params] {:root/fake-key [(make-image 2 "http://www.example.com/img.png")
                                                (make-comment 3 "Hi")
                                                (make-comment 8 "Other")]
                                :root/post     (make-image 2 "http://www.example.com/img.png")})}
  (dom/div nil
    (dom/button #js {:onClick #(prim/transact! this `[(route-to-8 {})])} "Route to Comment")
    (dom/button #js {:onClick #(prim/transact! this `[(route-to-2 {})])} "Route to Image")
    (dom/ul #js {:key react-key}
      (ui-stream-item post))))

(defcard-fulcro union-to-one
  Root
  {}
  {:inspect-data true})

(defsc ToManyRoot [this {:keys [ui/react-key root/posts]}]
  {:query         [:ui/react-key {:root/posts (prim/get-query StreamItemUnion)}]
   :initial-state (fn [params] {:root/posts [(make-comment 3 "Hi")
                                             (make-image 2 "http://www.example.com/img.png")
                                             (make-comment 8 "Other")]})}
  (dom/div nil
    (dom/ul #js {:key react-key}
      (map ui-stream-item posts))))

(defcard-fulcro union-to-many
  ToManyRoot
  {}
  {:inspect-data true})
