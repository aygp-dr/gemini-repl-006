(ns gemini-repl.main
  "Main entry point for Gemini REPL"
  (:require [gemini-repl.core.repl :as repl]))

(defn -main
  "Start the Gemini REPL"
  [& args]
  (println "Starting Gemini REPL...")
  (try
    (repl/start!)
    (catch Exception e
      (println "Error:" (.getMessage e))
      (System/exit 1))))

;; For Babashka script execution
(when (= *file* (System/getProperty "babashka.file"))
  (-main))
