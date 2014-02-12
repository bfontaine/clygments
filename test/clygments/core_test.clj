(ns clygments.core-test
  (:require [clojure.test :refer :all]
            [clygments.core :as clg]))

(deftest python-interpreter
  (is (= 16 (.getValue (.eval @#'clg/python "pow(4,2)")))))

