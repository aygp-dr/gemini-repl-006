(ns gemini-repl.utils.logger
  "Structured logging with file and FIFO support"
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def ^:private log-level (atom (keyword (str/lower-case (or (System/getenv "LOG_LEVEL") "info")))))
(def ^:private log-file (System/getenv "LOG_FILE"))
(def ^:private log-fifo (System/getenv "LOG_FIFO"))

(def ^:private levels {:debug 0 :info 1 :warn 2 :error 3})

(defn- should-log?
  "Check if message should be logged at current level"
  [level]
  (>= (get levels level 0) (get levels @log-level 0)))

(defn- format-log
  "Format log entry"
  [level data]
  (let [entry (merge {:timestamp (str (java.time.Instant/now))
                      :level (name level)}
                     data)]
    (json/generate-string entry)))

(defn- write-log
  "Write log entry to configured outputs"
  [entry]
  ;; Write to file
  (when log-file
    (io/make-parents log-file)
    (spit log-file (str entry "\n") :append true))
  
  ;; Write to FIFO (non-blocking)
  (when (and log-fifo (.exists (io/file log-fifo)))
    (try
      (spit log-fifo (str entry "\n") :append true)
      (catch Exception _
        ;; Ignore FIFO errors (no reader)
        nil))))

(defn log
  "Log a message at specified level"
  [level data]
  (when (should-log? level)
    (-> (format-log level data)
        (write-log))))

(defn debug [data] (log :debug data))
(defn info [data] (log :info data))
(defn warn [data] (log :warn data))
(defn error [data] (log :error data))

(defn get-level
  "Get current log level"
  []
  @log-level)

(defn set-level!
  "Set log level"
  [level]
  (reset! log-level level))
