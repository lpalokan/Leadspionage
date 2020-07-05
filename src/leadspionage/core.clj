(ns leadspionage.core
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [clojure.data.csv :as csv]
    [clojure.walk :as walk]
    [semantic-csv.core :as sc]
    [clj-time.core :as t]
    [clj-time.format :as f]
    )
  )

;; The basic logic of the app is as follows
;; Read the source file in Hubspot
(defn read-hubspot-source [source typeofsource]
  (if (= typeofsource "f")
    (do(
       println "Processing a file" source)
       (let [[header & rows] (-> source
                               io/file
                               io/reader
                               csv/read-csv)]
         (map #(-> (zipmap header %) (walk/keywordize-keys))  rows)
         )
        ) ;; header added
    (do(
         "directory"
         ;;(println "Determining suitable fils for " inputdirectory )
         (def directory (clojure.java.io/file source))
         (def files (file-seq directory))
         (doseq [filu files] (
                              (println filu)
                              ))))))

(defn hs-file [source]
     (let [[header & rows] (-> source
                             io/file
                             io/reader
                             csv/read-csv)]
       (map #(-> (zipmap header %) (walk/keywordize-keys))  rows)
       ))

(defn read-hubspot-file "Turn that source file into a map data structure" [source]
  (let [[header & rows] (-> source
                            io/file
                            io/reader
                            csv/read-csv)]
    (map #(-> (zipmap header %) (walk/keywordize-keys)) rows )))
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

(defn rhf [] (
                 read-hubspot-file "/Users/lpalokangas/Downloads/hubspot3.csv")
                  )
(def users (vec (rhf)))
(def complexdate (get-in (nth users 1000) [:LastActivityDate]))

;; (def df (java.text.SimpleDateFormat. "yyyy-mm-dd HH:mm"))
;;(.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (.parse df complexdate))
;; (get-in (nth users 1000) [:LastActivityDate])

(defn simplifydate [complexdate]
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd")
           (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") complexdate))
)

(simplifydate (get-in (nth (rhf) 1000) [:LastActivityDate]))
(simplifydate complexdate)
;; Today in the same date format
;; (defn todayis [] (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (new java.util.Date)))

(doseq [user users]
  ;;(println (get user :Email))
  (def email (get user :Email))
  ;;(println email)
  (println (str/split email #"@"))
  ;;(if (not-empty (nth (str/split email #"@") 1))
    ;;(println (nth str/split email #"@") 1))
  ;;  (println (not-empty? nth (str/split email #"@") 1))
  ;;(take-nth 0)
  )

  (transform-hs-import '(1 2 3 4))
(cohorts 1 2)
(summarize_cohorts '(1 2 3 4) '(1 2 3 4))