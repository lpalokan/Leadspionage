(ns leadspionage.leadspionage.exercises (:require [clojure.java.io :as io] [clojure-csv.core :as csv] [semantic-csv.core :as sc] [clojure.walk :as walk])))

(def sourcefile "/Users/lpalokangas/Downloads/hubspot2.csv")
(def csvfile (with-open [reader (io/reader sourcefile)]
               (doall
                 (csv/read-csv reader))))
;; (first csvfile)
;; (next csvfile)
(def header (first csvfile))
(def rows (rest csvfile))
(map #(-> (zipmap header %) (walk/keywordize-keys))  rows)
;; (def csvmap [map #(-> (zipmap header %) (walk/keywordize-keys))  rows])

(defn csvmap2
  [header & rows]
(map #(zipmap (map keyword header) %1) rows))