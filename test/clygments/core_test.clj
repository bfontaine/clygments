(ns clygments.core-test
  (:require [clojure.test :refer :all]
            [clygments.core :as clg]))

(deftest python-interpreter
  (is (= 16 (.getValue (.eval @#'clg/python "pow(4,2)")))))

(deftest exec-code-in
  (let [v "__foo42"]
    (testing "without default"
      (#'clg/exec-code-in v "41 + 1")
      (is (= 42 (.getValue (.get @#'clg/python v)))))
    (testing "with default value but no exception"
      (#'clg/exec-code-in v "41 + 1" "45")
      (is (= 42 (.getValue (.get @#'clg/python v)))))
    (testing "with default value"
      (#'clg/exec-code-in v "1/0" "45")
      (is (= 45 (.getValue (.get @#'clg/python v)))))))

(deftest py-true?
  (testing "True is True"
    (is (= (#'clg/py-true? "True") true)))
  (testing "Truthy is not True"
    (is (= (#'clg/py-true? "42") false)))
  (testing "a true expression is True"
    (is (= (#'clg/py-true? "1+1 == 2") true))))

(deftest highlight
  (testing "unknown language"
    (is (nil? (clg/highlight "foo = 2" :my-lang :html)))
    (is (nil? (clg/highlight "(+ 2 2)" :clowjur :html))))
  (testing "unknown output"
    (is (nil? (clg/highlight "(+ 2 2)" :clojure :foobar))))
  (testing ":null formatter"
    (is (= (clg/highlight "(+ 1 2)" :clojure :null) "(+ 1 2)\n"))))
