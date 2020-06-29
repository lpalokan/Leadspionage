(ns leadspionage.core
  (:require [clojure.java.io :as io] [clojure.data.csv :as csv] [clojure.walk :as walk]))

;; The basic logic of the app is as follows
;; Read the source file in Hubspot
(defn read-hubspot-source [source typeofsource]
  (if (= typeofsource "f")
    (do(
         println "Processing a file" source)
       (let [[header rows] (-> source
                               io/file
                               io/reader
                               csv/read-csv)]
         (map #(-> (zipmap header %) (walk/keywordize-keys))  rows)
         )
       )
    (do(
         "directory"
         ;;(println "Determining suitable fils for " inputdirectory )
         (def directory (clojure.java.io/file source))
         (def files (file-seq directory))
         (doseq [filu files] (
                              (println filu)
                              ))))))

;; Turn that source file into a map data structure
(comment "
Reduce the lead information:
Email address,
lifecycle status,
Last activity date,
Early stage score
This should likely be a map a la:
{ :email :status :lastactivity :score }
")
;; this should be a loop through every line where the date difference between
;; the last activity date and the time of processing the file is calculated and
;; added to the map
(defn transform-hs-import [imported_structure]
  (println "Here be transformation of" imported_structure "to a map")
  imported_structure)

;; Reduce this into cohorts based on the last activity date
;; Needs a subroutine that control how many cohorts and of what lenghts
;; Each cohort is an upper bound of how many days is it between today and the last activity date

(defn cohorts [date_today leads]
  (println "He be the cohorting of the data")
  (def leadcohorts [7 14 28 60 90 120 180])
  )
;; The only thing remaining is to summarize the results.
;; In: map with {:cohort}{:email}
;; The details of each Lead can be then found in leads{}
(defn summarize_cohorts [leadcohorts leads]
  (println "Here be a summary")
  )

(read-hubspot-source "/Users/lpalokangas/Downloads/hubspot.csv" "f")
(transform-hs-import '(1 2 3 4))
(cohorts 1 2)
(summarize_cohorts '(1 2 3 4) '(1 2 3 4))