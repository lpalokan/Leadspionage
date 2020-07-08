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

(defn cohorts [timesince]
  ;; Assigned to a cohort based on how long it's been since the last activity
  ;; Each cohort is an upper bound of how many days is it between today and the last activity date
  (def leadcohorts [7 14 28 60 90 120 180])
  ;; here be a switch-case for each cohort.
  )


(defn calclad [date_today user]
  ;; calculate the time since the last activity date for one user
    (def lastactivitydate (get user :LastActivityDate))
    (def timesince (java-time/time-between (hubspotdate2javadate lastactivitydate) date_today :days))
    ;; Here be a function that returns a cohort based on the timesince
    (assoc user :ActiveDaysAgo timesince) ;; this works
  )

;; The only thing remaining is to summarize the results.
;; In: map with [{:cohort}{:numberofusers}]
;; The details of each Lead can be then found in [users]
(defn summarize_cohorts [leadcohorts leads] (println "Here be a summary"))

(summarize_cohorts '(1 2 3 4) '(1 2 3 4))

;; If I am given one map, I want the same map back
(defn returnsamemap [maptomodify] (maptomodify))
;; Then, I want the same map with new information
(defn addnewkey [todayis maptomodify] (assoc maptomodify :ActiveDaysAgo "1"))

;; Then, I want the vector with multiple maps
(def userswithactivedaysago (map #(calclad todayis %) (rhf)))
;; You have to think inside out, not outside in
;; What is the smallest computation I can do
(nth userswithactivedaysago 10)