(ns clygments.core
  (:require [clojure.string :as str])
  (:import [org.python.util PythonInterpreter]
           [org.python.core PyObject]))

(def ^:private python
  (doto (PythonInterpreter.)
    (.exec "
from pygments import highlight
from pygments.lexers import get_lexer_by_name as get_lexer_by_name_
from pygments.formatters import get_formatter_by_name as get_formatter_by_name_

def get_lexer_by_name(name, opts):
    try:
        return get_lexer_by_name_(name, **opts)
    except:
        pass

def get_formatter_by_name(name, opts):
    try:
        return get_formatter_by_name_(name, **opts)
    except:
        pass

def run(code, lexer_name, lexer_opts, formatter_name, formatter_opts):
    lexer = get_lexer_by_name(lexer_name, lexer_opts)
    formatter = get_formatter_by_name(formatter_name, formatter_opts)

    if lexer and formatter:
        return highlight(code, lexer, formatter)")))

(defn- val->string
  "convert a simple Clojure value into a stringified Python one"
  [v]
  (cond
    (string? v)  (pr-str v)
    (nil? v)     "None"
    (true? v)    "True"
    (false? v)   "False"
    (number? v)  (str v)
    (char? v)    (recur (str v))
    (keyword? v) (recur (name v))))

(defn- keyword->argname
  "convert a Clojure keyword into an argument name for Pygments’ highlight
  function."
  [kw]
  (-> (name kw)
      (str/replace #"-" "")
      (str/lower-case)
      val->string))

(defn- keyword->langname
  "convert a keyword into a syntaxically valid language name for Pygments. This
   doesn’t ensure that the language is supported by Pygments."
  [kw]
  (-> (name kw)
      (str/lower-case)
      val->string))

(defn- map->kw-args
  "convert a map into a stringified list of Python keyword arguments"
  [m]
  (format "{%s}"
          (str/join "," (map (fn [[k v]]
                               (str (keyword->argname k) ":" (val->string v)))
                             m))))

(defn highlight
  "highlight a piece of code."
  ([code lang output] (highlight code lang output {}))
  ([code lang output opts]
   {:pre [(string? code)]}
   (let [opt-args (map->kw-args opts)
         ;; We give all options to both lexer and formatter;
         ;; unknown ones are ignored.
         python-code (format "run(%s, %s, %s, %s, %s)"
                             (val->string code)
                             (keyword->langname lang) opt-args
                             (keyword->langname output) opt-args)

         res ^PyObject (.eval ^PythonInterpreter python python-code)]

     (when (and res (.__nonzero__ res))
       ;; This won't work with non-text formatters, such as gif, but it's
       ;; ok since they're not supported on Jython (#2)
       (.asString res)))))
