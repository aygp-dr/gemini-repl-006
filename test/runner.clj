#!/usr/bin/env bb
(ns test.runner
  (:require [clojure.test :as t]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defn find-tests
  "Find all test namespaces"
  []
  (let [test-dir (io/file "test")]
    (->> (.listFiles test-dir)
         (filter #(.endsWith (.getName %) "_test.clj"))
         (map #(-> (.getName %)
                   (str/replace #"\.clj$" "")
                   (str/replace #"_" "-")
                   (->> (str "test."))
                   symbol)))))

(defn run-tests
  "Run all tests"
  []
  (let [namespaces (find-tests)]
    (doseq [ns namespaces]
      (require ns))
    (apply t/run-tests namespaces)))

(defn -main [& args]
  (let [{:keys [fail error]} (run-tests)]
    (System/exit (if (zero? (+ fail error)) 0 1))))

(when (= *file* (System/getProperty "babashka.file"))
  (-main))
