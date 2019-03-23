(ns clygments.core
  (:require [clojure.string :as str]
            [clython.core :as cly])
  (:import [org.python.core PyException]))

(defn- ignore-py-ex
  [f arg]
  (try
    (f arg)
    (catch PyException _ nil)))

(cly/let-import [pygments [highlight]
                 pygments.lexers [get-lexer-by-name guess-lexer]
                 pygments.formatters [get-formatter-by-name]]
  (defn- highlight*
    ; opts unsupported for now because get-*-by-name want kwargs-style args and
    ; we don't support those in our IFn reify thing
    [code lexer-name formatter-name opts]
    (let [lexer (if lexer-name
                  (ignore-py-ex get-lexer-by-name lexer-name)
                  (guess-lexer code))

          formatter (ignore-py-ex get-formatter-by-name formatter-name)]

      (when (and lexer formatter)
        (highlight code lexer formatter)))))

(defn- keyword->lowercase
  [kw]
  (-> kw name str/lower-case))

(defn highlight
  "highlight a piece of code."
  ([code lang output] (highlight code lang output nil))
  ([code lang output opts]
   {:pre [(string? code)]}
   (let [lexer-name (if (some? lang)
                      (keyword->lowercase lang))
         formatter-name (keyword->lowercase output)

         opts (->> opts
                   (map (fn [[k v]]
                          [(-> k name (str/replace #"-" "") str/lower-case)
                           v]))
                   (into {}))]

     (cly/jy->clj (highlight* code lexer-name formatter-name opts)))))
