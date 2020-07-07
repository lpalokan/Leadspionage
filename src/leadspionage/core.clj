(ns leadspionage.core
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [clojure.data.csv :as csv]
    [clojure.walk :as walk]
    [semantic-csv.core :as sc]
    )
  )

(refer-clojure :exclude [contains? iterate range min format zero? max])
(use 'java-time)

;; The basic logic of the app is as follows
(def latestsource "/Users/lpalokangas/Downloads/hubspot3.csv")
(defn read-hubspot-file "Turn that source file into a map data structure" [source]
  (let [[header & rows] (-> source
                            io/file
                            io/reader
                            csv/read-csv)]
    (map #(-> (zipmap header %) (walk/keywordize-keys)) rows )))

(defn latestfile [])
;; Read the source file in Hubspot

(defn rhf [] (read-hubspot-file latestsource))
;;(def users (vec (rhf)))

(defn read-hubspot-source [source typeofsource]
  (if (= typeofsource "f")
    (do(
        (read-hubspot-file source)
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


(defn simplifydate [complexdate]
  (str/split (.format (java.text.SimpleDateFormat. "yyyy-MM-dd")
                      (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") complexdate))
             #"-")
  )

(defn parse-int [s]
  (Integer. (re-find  #"\d+" s )))

(defn pick-year [complexdate] (nth complexdate 0))
(defn pick-month [complexdate] (nth complexdate 1))
(defn pick-day [complexdate] (nth complexdate 2))

(defn todayis [] (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (new java.util.Date)))
(def todayis (local-date))
(defn hubspotdate2javadate [lastactivitydate]
  ;;(println "Date to be parsed is " lastactivitydate)
  (local-date
    (parse-int (pick-year (simplifydate lastactivitydate)))
    (parse-int (pick-month (simplifydate lastactivitydate)))
    (parse-int (pick-day (simplifydate lastactivitydate)))
    )
  )


(defn cohorts [date_today leads]
  ;; this should be a loop through every line where the date difference between
  ;; the last activity date and the time of processing the file is calculated and
  ;; added to the map
  ;; Then, each user should be assigned to a cohort based on that date difference.
  (println "He be the cohorting of the data")
  ;; Each cohort is an upper bound of how many days is it between today and the last activity date
  (def leadcohorts [7 14 28 60 90 120 180])
  (doseq [[userindex user] (map-indexed vector leads)]
    ;;(println (get user :Email))
    (def lastactivitydate (get user :LastActivityDate))
    ;;(print lastactivitydate)
    (comment "(def lad_as_date
      (local-date
        (parse-int (pick-year (simplifydate lastactivitydate)))
        (parse-int (pick-month (simplifydate lastactivitydate)))
        (parse-int (pick-day (simplifydate lastactivitydate)))
        )
      )
      ")
    ;;(println (class lad_as_date))
    ;;(def timesince (java-time/time-between lad_as_date (local-date) :days))
    ;;(def timesince (java-time/time-between (hubspotdate2javadate lastactivitydate) (local-date) :days))
    (def timesince (java-time/time-between (hubspotdate2javadate lastactivitydate) date_today :days))
    ;;(def thisuser {:ActiveDaysAgo timesince})
    (assoc-in user [userindex :ActiveDaysAgo] timesince) ;; this works
    ;;(assoc-in user [userindex :ActiveDaysAgo] timesince)
    (if (= 0 (mod userindex 500)) (
                                    do(
                                      println "Time since: " timesince)
                                      (println (keys user) ;; well, it's immutable, remember
                                                )))
    ;; (println userindex " AND " timesince)
    ;;(println (str/split email #"@"))
    ;;(if (not-empty (nth (str/split email #"@") 1))
    ;;(println (nth str/split email #"@") 1))
    ;;  (println (not-empty? nth (str/split email #"@") 1))
    ;;(take-nth 0)
    ))

(cohorts (local-date) (vec (rhf)))

;; The only thing remaining is to summarize the results.
;; In: map with [{:cohort}{:numberofusers}]
;; The details of each Lead can be then found in [users]
(defn summarize_cohorts [leadcohorts leads] (println "Here be a summary"))

(summarize_cohorts '(1 2 3 4) '(1 2 3 4))



;;(def complexdate (get-in (nth users 1000) [:LastActivityDate]))
;; (def df (java.text.SimpleDateFormat. "yyyy-mm-dd HH:mm"))
;;(.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (.parse df complexdate))
;; (get-in (nth users 1000) [:LastActivityDate])

(comment"
(defn simplifydate [complexdate]
  (str/replace (.format (java.text.SimpleDateFormat. "yyyy-MM-dd")
           (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") complexdate))
  "-" " ")
  )
  ")


;;(simplifydate (get-in (nth (rhf) 1000) [:LastActivityDate]))
;;(simplifydate complexdate)

;; (local-date (parse-int "2020") (parse-int "03") (parse-int "24"))
(comment "
(def wasactive
  (local-date
    (parse-int (pick-year (simplifydate complexdate)))
    (parse-int (pick-month (simplifydate complexdate)))
    (parse-int (pick-day (simplifydate complexdate)))
                           )
  )
  ")

;;(println wasactive todayis2)
;;(java-time/time-between wasactive todayis2 :days)
;;(java-time/time-between (todayis) (simplifydate complexdate) :days)