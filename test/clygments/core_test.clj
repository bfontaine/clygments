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
    (is (= "(+ 1 2)\n" (clg/highlight "(+ 1 2)" :clojure :null)))
    (testing ":strip-nl option"
      (is (= "(+ 1 2)\n"
             (clg/highlight "\n\n(+ 1 2)\n" :clojure :null {:strip-nl true})))
      (is (= "\n\n(+ 1 2)\n"
             (clg/highlight "\n\n(+ 1 2)\n" :clojure :null {:strip-nl false}))))
    (testing ":tab-size option"
      (is (= "  (+ 1 2)\n"
             (clg/highlight "\t(+ 1 2)" :clojure :null {:tab-size 2}))))
    (testing "python syntax"
      (let [s "def f():\n  \"\"\"\n  doc\n  \"\"\"\n  pass\n"]
        (is (= s (clg/highlight s :python :null))))
      (let [s "a = \"foo\"\n"]
        (is (= s (clg/highlight s :python :null))))
      (let [s "a = \"\\\"\"\n"]
        (is (= s (clg/highlight s :python :null))))))

  (testing "guess language"
    (let [code "
import foo
# hey
def main():
  print(\"hey\")
"]
      (is (= (clg/highlight code :python2 :html)
             (clg/highlight code nil :html))))))
