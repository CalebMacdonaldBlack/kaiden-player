(ns kaiden-player.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [kaiden-player.core-test]
            [kaiden-player.views.test-route-template]
            [kaiden-player.test-handlers]
            [kaiden-player.test-subscribers]))

(doo-tests 'kaiden-player.core-test
           'kaiden-player.views.test-route-template
           'kaiden-player.test-handlers
           'kaiden-player.test-subscribers)


