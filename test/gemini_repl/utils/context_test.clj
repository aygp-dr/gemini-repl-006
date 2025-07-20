(ns test.gemini-repl.utils.context-test
  (:require [clojure.test :refer [deftest is testing]]
            [gemini-repl.utils.context :as ctx]))

(deftest test-context-management
  (testing "Adding messages"
    (ctx/clear!)
    (ctx/add-message :user "Hello")
    (ctx/add-message :assistant "Hi there!")
    (is (= 2 (count (ctx/get-messages))))
    (is (pos? (ctx/get-token-count)))))

(deftest test-context-trimming
  (testing "Context stays within limits"
    (ctx/clear!)
    ;; Add many messages
    (dotimes [i 100]
      (ctx/add-message :user (str "Message " i)))
    ;; Should be trimmed
    (is (< (ctx/get-token-count) 100000))))
