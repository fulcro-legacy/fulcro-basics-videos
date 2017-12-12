(ns demo.client-test-main
  (:require demo.tests-to-run
            [fulcro-spec.selectors :as sel]
            [fulcro-spec.suite :as suite]))

(enable-console-print!)

(suite/def-test-suite client-tests {:ns-regex #"demo..*-spec"}
  {:default   #{::sel/none :focused}
   :available #{:focused}})

