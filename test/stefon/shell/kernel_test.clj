(ns stefon.shell.kernel-test
  (:require [midje.sweet :refer :all]
            [lamina.core :as lamina]

            [stefon.shell :as shell]
            [stefon.shell.plugin :as plugin]
            [stefon.shell.kernel :as kernel]))



(against-background [#_(before :facts (shell/start-system))
                     #_(after :facts (shell/stop-system))]


                    (fact "Test kernel receive from plugin message"

                          (let [system (plugin/create-plugin-system (shell/create-system))

                                handler (fn [inp] inp)
                                sender (plugin/attach-plugin system handler)

                                satom (kernel/attach-kernel system)

                                xxx (sender {:fu :bar})]

                            )
                          1 => 1)

                    (fact "Test kernel action map from plugin message"
                          2 => 2)
)
