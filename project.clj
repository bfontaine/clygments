(defproject clygments "2.0.0-clython-SNAPSHOT"
  :description "Use Pygments from Clojure"
  :url "https://github.com/bfontaine/clygments"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure          "1.8.0"]
                 [org.python/jython-standalone "2.7.1"]
                 [org.pygments/pygments        "2.3.1"]
                 [clython                      "0.0.1"]]
  :plugins [[lein-cloverage "1.0.9"]])
