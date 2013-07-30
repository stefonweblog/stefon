(ns stefon.plugin-test
  (:require [midje.sweet :refer :all]
            [lamina.core :as lamina]

            [stefon.plugin :as plugin]))


#_(against-background [(before :facts 1)
                     (after :facts 2)]


                    ;; ====
                    #_(let [[client server] (lamina/channel-pair)]

                      (fact ""
                            1 => 1))


)
