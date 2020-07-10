(ns leadspionage.core
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [clojure.data.csv :as csv]
    [clojure.walk :as walk]
    [semantic-csv.core :as sc]
    [com.hypirion.clj-xchart :as cl]
    )
  )

(use 'java-time)
(refer-clojure :exclude [contains? iterate range min format zero? max])

;; The basic logic of the app is as follows
;; Define source file from Hubspot
(def latestsource "/Users/lpalokangas/Downloads/hubspot3.csv")
(defn read-hubspot-file "Turn that source file into a map data structure" [source]
  ;; First, read a CSV file in from Hubspot.
  (let [[header & rows]
        (-> source
                            io/file
                            io/reader
                            csv/read-csv)]
    (map #(-> (zipmap header %) (walk/keywordize-keys)) rows )))

(defn rhf [] (read-hubspot-file latestsource))

(defn hsdate->javadate [lastactivitydate]
  ;; Convert date format from Hubspot's string to Java date
  (def shortdate (str/replace lastactivitydate #"(\d{4})-(\d{2})-(\d{2}).+" "$1 $2 $3"))
  (def datearray (str/split shortdate #" "))
  (apply local-date (map #(Integer/parseInt %) datearray))
  )


(def todayis
  ;; Specify what day is it today.
  (local-date)
  )

(defn cohorts [timesince]
  ;; Assign each lead to a cohort based on how long it's been since the last activity
  ;; Each cohort is an upper bound of how many days is it between today and the last activity date
  (let [howlong timesince]
    (cond
      (<= howlong 7) 7
      (<= howlong 14) 14
      (<= howlong 28) 28
      (<= howlong 60) 60
      (<= howlong 120) 120
      (<= howlong 180) 180
      :else 360
      )
    )
  )

(defn calclad [date_today user]
  ;; calculate the time since the last activity date for one user
  (def lastactivitydate (get user :LastActivityDate))
  (def timesince (java-time/time-between (hsdate->javadate lastactivitydate) date_today :days))
  (assoc user :ActiveDaysAgo timesince :ActivityCohort (cohorts timesince))
  )

(def userswithactivedaysago (map #(calclad todayis %) (rhf)))

;; Summarize the results.
(defn summarize_cohorts [leads] (
                sort (frequencies (map #(get % :ActivityCohort) leads)))
               )

(summarize_cohorts userswithactivedaysago)

;; Build a chart out of it
;; Here be keys to the chart
(def xvalues (vec (keys (summarize_cohorts userswithactivedaysago))))
;; Here be values
(def yvalues (vec (vals (summarize_cohorts userswithactivedaysago))))

(defn freq-chart [xvalues yvalues]
  (def chart
    (cl/category-chart {"Time since last activity" {
                                              :x xvalues
                                              :y yvalues}}
                 {
                  :annotations? true
                  :title "Drop-off rate of leads"
                  :legend {:visible? true :position :inside-ne}
                  :x-axis {:order (reverse xvalues)}}))
  (cl/view chart)
      )

(freq-chart xvalues yvalues)