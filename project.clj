(defproject leadspionage "0.1.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.csv "1.0.0"]
                 [semantic-csv "0.2.1-alpha1"]
                 [clj-time "0.15.2"]
                 [clojure.java-time "0.3.2"]
                 [com.hypirion/clj-xchart "0.2.0"]
                 ]
  :repl-options {:init-ns leadspionage.core})
