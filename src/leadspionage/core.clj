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
(def latest-source "/Users/lpalokangas/Downloads/hubspot3.csv")
(defn read-hubspot-file "Turn that source file into a map data structure" [source]
  ;; First, read a CSV file in from Hubspot.
  (let [[header & rows]
        (-> source
                            io/file
                            io/reader
                            csv/read-csv)]
    (map #(-> (zipmap header %) (walk/keywordize-keys)) rows )))

(defn hsdate->javadate [last-activity-date]
  ;; Convert date format from Hubspot's string to Java date
  (def short-date (str/replace last-activity-date #"(\d{4})-(\d{2})-(\d{2}).+" "$1 $2 $3"))
  (def date-as-array (str/split short-date #" "))
  (apply local-date (map #(Integer/parseInt %) date-as-array))
  )

(defn cohort-from-days-since-active [how-long]
  ;; Assign each lead to a cohort based on how long it's been since the last activity
  ;; Each cohort is an upper bound of how many days is it between today and the last activity date
  (cond
    (<= how-long 7) 7
    (<= how-long 14) 14
    (<= how-long 28) 28
    (<= how-long 60) 60
    (<= how-long 120) 120
    (<= how-long 180) 180
    :else 360
    )
  )

(defn calculate-last-activity-date [date_today user]
  ;; calculate the time since the last activity date for one user
  (def last-activity-date (get user :LastActivityDate))
  (def time-since (java-time/time-between (hsdate->javadate last-activity-date) date_today :days))
  (assoc user :ActiveDaysAgo time-since :ActivityCohort (cohort-from-days-since-active time-since))
  )

(def users-with-active-days-ago (map #(calculate-last-activity-date (local-date) %) (read-hubspot-file latest-source)))

;; Summarize the results.
(defn summarize-cohorts [leads] (
                sort (frequencies (map #(get % :ActivityCohort) leads)))
               )

(summarize-cohorts users-with-active-days-ago)

;; Build a chart out of it
;; Here be keys to the chart
(def x-values (vec (keys (summarize-cohorts users-with-active-days-ago))))
;; Here be values
(def y-values (vec (vals (summarize-cohorts users-with-active-days-ago))))

(defn freq-chart [x-values y-values]
  (def chart
    (cl/category-chart {"Time since last activity" {
                                              :x x-values
                                              :y y-values}}
                 {
                  :annotations? true
                  :title "Drop-off rate of leads"
                  :legend {:visible? true :position :inside-ne}
                  :x-axis {:order (reverse x-values)}}))
  (cl/view chart)
      )

(freq-chart x-values y-values)