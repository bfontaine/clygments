(ns clygments.core-test
  (:require [clojure.test :refer :all]
            [clygments.core :as clg]))

(deftest keyword->argname
  (testing "one word"
    (is (= (#'clg/keyword->argname :foo) "\"foo\"")))
  (testing "capitalized"
    (is (= (#'clg/keyword->argname :Foo) "\"foo\"")))
  (testing "with an hyphen"
    (is (= (#'clg/keyword->argname :foo-bar) "\"foobar\"")))
  (testing "with an hyphen and capitalized"
    (is (= (#'clg/keyword->argname :Foo-Bar) "\"foobar\"")))
  (testing "string"
    (is (= (#'clg/keyword->argname "Foo-Bar") "\"foobar\""))))

(deftest keyword->langname
  (testing "one word"
    (is (= (#'clg/keyword->langname :foo) "\"foo\"")))
  (testing "capitalized"
    (is (= (#'clg/keyword->langname :Foo) "\"foo\"")))
  (testing "with an hyphen"
    (is (= (#'clg/keyword->langname :foo-bar) "\"foo-bar\"")))
  (testing "with an hyphen and capitalized"
    (is (= (#'clg/keyword->langname :Foo-Bar) "\"foo-bar\"")))
  (testing "string"
    (is (= (#'clg/keyword->langname "Foo-Bar") "\"foo-bar\""))))

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
    (is (= (#'clg/map->kw-args {}) "{}")))
  (testing "string->string"
    (is (= (#'clg/map->kw-args {"foo" "bar"}) "{\"foo\":\"bar\"}"))
    (is (= (#'clg/map->kw-args {"foo" "b\\ar"}) "{\"foo\":\"b\\\\ar\"}"))
    (is (= (#'clg/map->kw-args {"foo" "bar\\n"}) "{\"foo\":\"bar\\\\n\"}")))
  (testing "keyword->string"
    (is (= (#'clg/map->kw-args {:foo "bar"}) "{\"foo\":\"bar\"}")))
  (testing "various types"
    (is (= (#'clg/map->kw-args (sorted-map :a "a" :b 42.0 :c nil))
           "{\"a\":\"a\",\"b\":42.0,\"c\":None}"))))

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
