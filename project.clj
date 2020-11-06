(defproject clygments "2.0.2-SNAPSHOT"
  :description "Use Pygments from Clojure"
  :url "https://github.com/bfontaine/clygments"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.python/jython-standalone "2.7.1"]
                 ;; Pygments 2.6+ doesn't support Python2 anymore, so we'll have to ditch Jython in favor of GraalVM
                 [org.pygments/pygments        "2.5.2"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.1"]]}}
  :global-vars {*warn-on-reflection* true}
  :plugins [[lein-cloverage "1.0.9"]])
