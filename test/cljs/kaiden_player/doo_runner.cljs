(ns kaiden-player.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [kaiden-player.core-test]))

(doo-tests 'kaiden-player.core-test
           'kaiden-player.test-handlers
           'kaiden-player.test-subscribers
           'kaiden-player.views.test-route-template)


