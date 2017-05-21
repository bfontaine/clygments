(defproject clygments "0.1.2-SNAPSHOT"
  :description "Use Pygments from Clojure"
  :url "https://github.com/bfontaine/clygments"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure          "1.8.0"]
                 [org.python/jython-standalone "2.7.0"]
                 [org.pygments/pygments        "2.1.3"]]
  :plugins [[lein-cloverage "1.0.9"]])
