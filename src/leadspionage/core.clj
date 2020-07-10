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
(def latestsource "/Users/lpalokangas/Downloads/hubspot3.csv")
(defn read-hubspot-file "Turn that source file into a map data structure" [source]
  (let [[header & rows] (-> source
                            io/file
                            io/reader
                            csv/read-csv)]
    (map #(-> (zipmap header %) (walk/keywordize-keys)) rows )))

(defn rhf [] (read-hubspot-file latestsource))

(defn hsdate->javadate [lastactivitydate]
  ;;(println "Working on: " lastactivitydate)
  (def shortdate (str/replace lastactivitydate #"(\d{4})-(\d{2})-(\d{2}).+" "$1 $2 $3"))
  (def datearray (str/split shortdate #" "))
  (apply local-date (map #(Integer/parseInt %) datearray))
  )

(def todayis (local-date))

(defn cohorts [timesince]
  ;; Assigned to a cohort based on how long it's been since the last activity
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
  ;; here be a switch-case for each cohort.
  )

(defn calclad [date_today user]
  ;; calculate the time since the last activity date for one user
  (def lastactivitydate (get user :LastActivityDate))
  (def timesince (java-time/time-between (hsdate->javadate lastactivitydate) date_today :days))
  (assoc user :ActiveDaysAgo timesince :ActivityCohort (cohorts timesince))
  ;; Next, figure out the cohort based on the timesince
  ;;(assoc user :ActivityCohort (cohorts timesince))
  )

(def userswithactivedaysago (map #(calclad todayis %) (rhf)))

;; The only thing remaining is to summarize the results.
;; In: map with [{:cohort}{:numberofusers}]
;; The details of each Lead can be then found in [users]
(defn summarize_cohorts [leads] (
               ;;frequencies (map #(get % :ActivityCohort) leads))
               ;;reverse (sort (frequencies (map #(get % :ActivityCohort) leads))))
                sort (frequencies (map #(get % :ActivityCohort) leads)))
               )

(summarize_cohorts userswithactivedaysago)
;; Here be keys to the chart
(def xvalues (vec (keys (summarize_cohorts userswithactivedaysago))))
;; Here be values
(def yvalues (vec (vals (summarize_cohorts userswithactivedaysago))))

(defn freq-chart [xvalues yvalues]
  ;; (def chart
  ;;  (cl/xy-chart {"Leads drop off the wagon" xvalues yvalues]}))
  (def chart
    (cl/category-chart {"Leads drop off the wagon" {
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

;; Here create a data structure where ledas are grouped by their ActivityCohort
;;(map #(get % :ActivityCohort) userswithactivedaysago)
;;(frequencies (map #(get % :ActivityCohort) userswithactivedaysago))
;;(def groupleadsbyactivedays (group-by #(get % :ActivityCohort) userswithactivedaysago))
;;(frequencies groupleadsbyactivedays)
;; (summarize_cohorts '(1 2 3 4))
