(ns stefon.action-test
  (:use midje.sweet)
  (:require [stefon.action :as action]
            [stefon.shell :as shell]))


(against-background [(before :facts (shell/start-system))]


                    (fact "Creating a Post"

                          ))
