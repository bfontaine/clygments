(ns clygments.core
  (:require [clojure.string :as str])
  (:import [org.python.util PythonInterpreter]
           [org.python.core PyObject PyNone PyBoolean PyLong PyFloat
                            PyDictionary PyUnicode]
           [java.util HashMap]
           [clojure.lang Keyword PersistentArrayMap PersistentHashMap]))

(def ^:private python
  (doto (PythonInterpreter.)
    (.exec "
from pygments import highlight
from pygments.lexers import get_lexer_by_name, guess_lexer
from pygments.formatters import get_formatter_by_name
from pygments.util import ClassNotFound

def get_lexer(name, opts):
    try:
        return get_lexer_by_name(name, **opts)
    except:
        pass

def get_formatter(name, opts):
    try:
        return get_formatter_by_name(name, **opts)
    except:
        pass

def run(code, lexer_name, formatter_name, opts):
    if lexer_name is None:
        try:
           lexer = guess_lexer(code)
        except ClassNotFound:
           return
    else:
        lexer = get_lexer(lexer_name, opts)

    formatter = get_formatter(formatter_name, opts)

    if lexer and formatter:
        return highlight(code, lexer, formatter)")))

(def ^:private
  py-run-fn
  (.eval python "run"))

(def ^:private
  py-none (.eval ^PythonInterpreter python "None"))

(defmulti ^:private clj->jy type)

(defmethod clj->jy nil [_] py-none)

(let [py-true (PyBoolean. true)
      py-false (PyBoolean. false)]
  (defmethod clj->jy Boolean [x]
    (if x
      py-true
      py-false)))

(defmethod clj->jy String [s] (PyUnicode. ^String s))
(defmethod clj->jy Integer [i] (PyLong. ^Long (long i)))
(defmethod clj->jy Long [l] (PyLong. ^Long l))
(defmethod clj->jy Float [f] (PyFloat. ^Float f))
(defmethod clj->jy Double [d] (PyFloat. ^Double d))
(defmethod clj->jy Character [c] (PyUnicode. (str c)))
(defmethod clj->jy Keyword [k] (PyUnicode. (name k)))

(defn- map->jy
  ([m]
   (map->jy m identity))
  ([m keyfn]
   (let [hm (HashMap. (count m))]
     (doseq [[k v] m]
       (.put hm (clj->jy (keyfn k)) (clj->jy v)))
     (PyDictionary. hm))))

(defmethod clj->jy PersistentHashMap [m] (map->jy m))
(defmethod clj->jy PersistentArrayMap [m] (map->jy m))

(defn- keyword->lowercase-pystring
  [kw]
  (-> kw name str/lower-case (PyUnicode.)))

(defn highlight
  "highlight a piece of code."
  ([code lang output] (highlight code lang output nil))
  ([code lang output opts]
   {:pre [(string? code)]}
   (let [py-code (PyUnicode. ^String code)
         py-lexer-name (if (some? lang)
                         (keyword->lowercase-pystring lang)
                         py-none)
         py-formatter-name (keyword->lowercase-pystring output)

         py-opts (map->jy opts
                          (fn [k]
                            (-> k name (str/replace #"-" "") str/lower-case)))

         res (.__call__ py-run-fn
                        (into-array PyObject [py-code
                                              py-lexer-name
                                              py-formatter-name
                                              py-opts]))]

     (when (and res (.__nonzero__ res))
       ;; This won't work with non-text formatters, such as gif, but it's
       ;; ok since they're not supported on Jython (#2)
       (.asString res)))))
