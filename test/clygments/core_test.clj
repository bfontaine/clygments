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
    (is (= (#'clg/py-true? "1+1 == 2") true))
    (is (= (#'clg/py-true? "1+1 != 2") false)))
  (testing "multiple expressions"
    (is (= (#'clg/py-true? "1+1 == 2" "2+2 == 4") true))
    (is (= (#'clg/py-true? "1+1 == 2" "2+1 == 4") false))))

(deftest escape-string
  (testing "empty string"
    (is (= (#'clg/escape-string "") "")))
  (testing "no special chars"
    (let [s "foobar 42 #$"]
      (is (= (#'clg/escape-string s) s))))
  (testing "backslashes only"
    (is (= (#'clg/escape-string "here: \\") "here: \\\\")))
  (testing "special \\-chars"
    (is (= (#'clg/escape-string "newline: \\n") "newline: \\\\n")))
  (testing "quotes"
    (is (= (#'clg/escape-string "a \"string\".") "a \\\"string\\\"."))))

(deftest keyword->argname
  (testing "one word"
    (is (= (#'clg/keyword->argname :foo) "foo")))
  (testing "capitalized"
    (is (= (#'clg/keyword->argname :Foo) "foo")))
  (testing "with an hyphen"
    (is (= (#'clg/keyword->argname :foo-bar) "foobar")))
  (testing "with an hyphen and capitalized"
    (is (= (#'clg/keyword->argname :Foo-Bar) "foobar")))
  (testing "string"
    (is (= (#'clg/keyword->argname "Foo-Bar") "foobar"))))

(deftest val->string
  (testing "nil"
    (is (= (#'clg/val->string nil) "None")))
  (testing "boolean"
    (is (= (#'clg/val->string true) "True"))
    (is (= (#'clg/val->string false) "False")))
  (testing "integer"
    (is (= (#'clg/val->string 0) "0"))
    (is (= (#'clg/val->string 42) "42"))
    (is (= (#'clg/val->string -12) "-12")))
  (testing "float"
    (is (= (#'clg/val->string 0.0) "0.0"))
    (is (= (#'clg/val->string 42.56) "42.56"))
    (is (= (#'clg/val->string -12.345) "-12.345")))
  (testing "string"
    (is (= (#'clg/val->string "foo") "\"foo\""))
    (is (= (#'clg/val->string "bar\\foo") "\"bar\\\\foo\""))
    (is (= (#'clg/val->string "x\\n") "\"x\\\\n\"")))
  (testing "char"
    (is (= (#'clg/val->string \a) "\"a\"")))
  (testing "keyword"
    (is (= (#'clg/val->string :foo) "\"foo\""))
    (is (= (#'clg/val->string :12) "\"12\"")))
  (testing "list"
    (is (nil? (#'clg/val->string '(1 2 3))))))

(deftest map->kw-args
  (testing "empty map"
    (is (= (#'clg/map->kw-args {}) "")))
  (testing "string->string"
    (is (= (#'clg/map->kw-args {"foo" "bar"}) "foo=\"bar\""))
    (is (= (#'clg/map->kw-args {"foo" "b\\ar"}) "foo=\"b\\\\ar\""))
    (is (= (#'clg/map->kw-args {"foo" "bar\\n"}) "foo=\"bar\\\\n\"")))
  (testing "keyword->string"
    (is (= (#'clg/map->kw-args {:foo "bar"}) "foo=\"bar\"")))
  (testing "various types"
    (is (= (#'clg/map->kw-args (sorted-map :a "a" :b 42.0 :c nil))
           "a=\"a\",b=42.0,c=None"))))

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
             "  (+ 1 2)\n")))))
