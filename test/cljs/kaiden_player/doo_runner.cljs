(ns kaiden-player.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [kaiden-player.core-test]))

(doo-tests 'kaiden-player.core-test)

