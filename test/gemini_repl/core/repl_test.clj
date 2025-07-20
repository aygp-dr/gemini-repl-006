(ns test.gemini-repl.core.repl-test
  (:require [clojure.test :refer [deftest is testing]]
            [gemini-repl.core.repl :as repl]))

(deftest test-command-parsing
  (testing "Command parsing"
    (is (= :help (repl/parse-command "/help")))
    (is (= :exit (repl/parse-command "/exit")))
    (is (= :unknown (repl/parse-command "/invalid")))))

(deftest test-prompt-generation
  (testing "Prompt generation"
    (with-redefs [gemini-repl.utils.context/get-token-count (constantly 42)]
      (is (= "\n[42 tokens] > " (repl/get-prompt))))))
