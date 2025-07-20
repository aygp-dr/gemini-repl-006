(ns test.gemini-repl.tools.system-test
  (:require [clojure.test :refer [deftest is testing]]
            [gemini-repl.tools.system :as tools]
            [clojure.java.io :as io]))

(deftest test-safe-path
  (testing "Path safety checks"
    (is (tools/safe-path? "test.txt"))
    (is (tools/safe-path? "subdir/test.txt"))
    (is (not (tools/safe-path? "../outside.txt")))
    (is (not (tools/safe-path? "/etc/passwd")))))

(deftest test-file-operations
  (testing "File read/write"
    (let [result (tools/execute :write_file {:path "test.txt" :content "Hello"})]
      (is (:success result)))
    (let [result (tools/execute :read_file {:path "test.txt"})]
      (is (:success result))
      (is (= "Hello" (:content result))))))
