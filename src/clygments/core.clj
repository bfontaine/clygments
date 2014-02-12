(ns clygments.core
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
  "test if an expression evals to True in Python"
  [expr]
  (= (.eval python expr) py-true))

(defn highlight
  "highlight a piece of code."
  [code lang output]
    (try
      (exec-code-in "_r" (str "get_lexer_by_name(\"" (name lang) "\")") "None")
      (exec-code-in
        "_f" (str "get_formatter_by_name(\"" (name output) "\")") "None")
      (if (and (py-true? "_r!=None") (py-true? "_f!=None"))
        (do
          ;; XXX it won't work if 'code contains a sequence of three or more
          ;; double quotes, e.g.: """
          (.exec python (str "_res=highlight(\"\"\"" code "\"\"\",_r,_f)\n"))
          ;; FIXME it won't work with non-text formatters, such as gif
          (.get python "_res" String)))
      (catch Exception e
        (.printStackTrace e)
        (println (.getMessage e)))))
