(ns demo.client
  (:require [fulcro.client :as fc]))

(defonce app (atom (fc/new-fulcro-client)))
