(ns clygments.core
  (:require [clojure.string :as cs])
  (:import [org.python.util PythonInterpreter]))

(def ^{:private true} python
  (doto (PythonInterpreter.)
    (.exec (str "from pygments import highlight\n"
                "from pygments.lexers import *\n"
                "from pygments.formatters import *\n"))))

(defn- exec-code-in
  "put the result of the execution of a line of code in a variable, with
   an optional default value if it encounters an exception"
  ([var-name code]
    (.exec python (str var-name "=" code)))
  ([var-name code default]
    (.exec python (str "try:\n\t" var-name "=" code "\n"
                       "except:\n\t" var-name "=" default "\n"))))

(def ^{:private true} py-true (.eval python "True"))
(defn- py-true?
  "test if one or more expressions eval to True in Python"
  [& exprs]
  (every? #(= (.eval python %) py-true) exprs))

(defn- escape-string
  "escape backslashes and double quotes in a string"
  [s]
  (-> s
    (cs/replace #"\\" "\\\\\\\\") ; replace \ with \\
    (cs/replace #"\"" "\\\\\""))) ; replace " with \"

(defn- keyword->argname
  "convert a Clojure keyword into an argument name for Pygments’ highlight
   function."
  [kw]
  (-> (name kw)
    (cs/replace #"-" "")
    (.toLowerCase)))

(defn- keyword->langname
  "convert a keyword into a syntaxically valid language name for Pygments. This
   doesn’t ensure that the language is supported by Pygments."
  [k]
  (.toLowerCase (name k)))

(defn- val->string
  "convert a simple Clojure value into a stringified Python one"
  [v]
  (cond
    (nil? v)     "None"
    (true? v)    "True"
    (false? v)   "False"
    (number? v)  (str v)
    (string? v)  (str \" (escape-string v) \")
    (char? v)    (recur (str v))
    (keyword? v) (recur (name v))))

(defn- map->kw-args
  "convert a map into a stringified list of Python keyword arguments"
  [m]
  (cs/join "," (map (fn [[k v]]
                      (str (keyword->argname k) "=" (val->string v))) m)))

(defn highlight
  "highlight a piece of code."
  ([code lang output] (highlight code lang output {}))
  ([code lang output opts]
    (try
      (let [opt-args (map->kw-args opts)]
        ;; we give all options to both lexer and formatter, unknown ones will
        ;; be ignored
        (exec-code-in "_r" (format "get_lexer_by_name(\"%s\",%s)"
                                   (keyword->langname lang) opt-args) "None")
        (exec-code-in "_f" (format "get_formatter_by_name(\"%s\",%s)"
                                   (keyword->langname output) opt-args) "None")
        (if (py-true? "_r!=None" "_f!=None")
          (do
            (.exec python (str "_res=highlight(\"\"\"" (escape-string code)
                               "\"\"\",_r,_f)\n"))
            ;; FIXME it won't work with non-text formatters, such as gif (#2)
            (.get python "_res" String))))
      (catch Exception e
        (.printStackTrace e)
        (println (.getMessage e))))))
