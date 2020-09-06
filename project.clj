(defproject clygments "2.0.1"
  :description "Use Pygments from Clojure"
  :url "https://github.com/bfontaine/clygments"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure          "1.8.0"]
                 [org.python/jython-standalone "2.7.1"]
                 ;; Pygments 2.6+ doesn't support Python2 anymore, so we'll have to ditch Jython in favor of GraalVM
                 [org.pygments/pygments        "2.5.2"]]
  :plugins [[lein-cloverage "1.0.9"]])
