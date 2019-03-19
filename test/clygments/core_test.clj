(ns clygments.core-test
  (:require [clojure.test :refer :all]
            [clygments.core :as clg]))

(deftest highlight
  (testing "unknown language"
    (is (nil? (clg/highlight "foo = 2" :my-lang :html)))
    (is (nil? (clg/highlight "(+ 2 2)" :clowjur :html))))
  (testing "unknown output"
    (is (nil? (clg/highlight "(+ 2 2)" :clojure :foobar))))
  (testing ":null formatter"
    (is (= (clg/highlight "(+ 1 2)" :clojure :null) "(+ 1 2)\n"))
    (testing ":strip-nl option"
      (is (= (clg/highlight "\n\n(+ 1 2)\n" :clojure :null {:strip-nl true})
             "(+ 1 2)\n"))
      (is (= (clg/highlight "\n\n(+ 1 2)\n" :clojure :null {:strip-nl false})
             "\n\n(+ 1 2)\n")))
    (testing ":tab-size option"
      (is (= (clg/highlight "\t(+ 1 2)" :clojure :null {:tab-size 2})
             "  (+ 1 2)\n")))
    (testing "python syntax"
      (let [s "def f():\n  \"\"\"\n  doc\n  \"\"\"\n  pass\n"]
        (is (= (clg/highlight s :python :null) s)))
      (let [s "a = \"foo\"\n"]
        (is (= (clg/highlight s :python :null) s)))
      (let [s "a = \"\\\"\"\n"]
        (is (= (clg/highlight s :python :null) s))))))
