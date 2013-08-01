(ns stefon.shell.kernel-plugin-test
  (:require [midje.sweet :refer :all]
            [lamina.core :as lamina]

            [stefon.shell :as shell]
            [stefon.shell.plugin :as plugin]
            [stefon.shell.kernel :as kernel]))

(against-background []
(fact "Test kernel receive from plugin message"

      (let [system (plugin/create-plugin-system (shell/create-system))
            handler (fn [inp] inp)
            sender (plugin/attach-plugin system handler)

            result-event (atom nil)
            kernel-handler (fn [inp] (swap! result-event (fn [i] inp)))

            xxx (kernel/attach-kernel system kernel-handler)
            xxy (sender {:fu :bar :qwerty :board})]

        @result-event =not=> nil?
        (type @result-event) => clojure.lang.PersistentArrayMap
        (keys @result-event) => (contains #{:fu :qwerty})))


(fact "Test attaching kernel to the shell"

      (let [system (shell/create-system)
            result-event (atom nil)
            kernel-handler (fn [inp] (swap! result-event (fn [i] inp)))

            system-with-handler (shell/start-system system kernel-handler)

            ;; something needs to send an event
            handler (fn [inp] inp)
            sender (plugin/attach-plugin @system-with-handler handler)

            result-send (sender {:fu :bar :qwerty :board})]

        @result-event =not=> nil?
        (type @result-event) => clojure.lang.PersistentArrayMap
        (keys @result-event) => (contains #{:fu :qwerty})
        ))


(fact "Test kernel action map from plugin message"
      2 => 2)
      )

