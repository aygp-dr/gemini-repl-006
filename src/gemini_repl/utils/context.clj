(ns gemini-repl.utils.context
  "Conversation context management"
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(def ^:private context (atom {:messages []
                              :token-count 0
                              :start-time (System/currentTimeMillis)}))

(def ^:private max-tokens (parse-long (or (System/getenv "MAX_CONTEXT_TOKENS") "100000")))

(defn- estimate-tokens
  "Estimate token count for text (rough approximation)"
  [text]
  (int (/ (count text) 4)))

(defn- trim-context
  "Trim context to stay within token limits"
  [messages current-tokens]
  (if (<= current-tokens max-tokens)
    messages
    (let [keep-count (max 2 (int (/ (count messages) 2)))]
      (vec (concat (take 1 messages)  ; Keep system message
                   (take-last keep-count messages))))))

(defn add-message
  "Add a message to the context"
  [role content]
  (let [new-msg {:role role
                 :content content
                 :timestamp (System/currentTimeMillis)}
        tokens (estimate-tokens content)]
    (swap! context update :messages conj new-msg)
    (swap! context update :token-count + tokens)
    (swap! context update :messages trim-context (:token-count @context))))

(defn add-tool-response
  "Add a tool response to the context"
  [tool-name result]
  (add-message :tool (str "Tool " tool-name " result: " result)))

(defn get-messages
  "Get all messages in context"
  []
  (:messages @context))

(defn get-token-count
  "Get current token count"
  []
  (:token-count @context))

(defn get-stats
  "Get context statistics"
  []
  (let [ctx @context
        duration (- (System/currentTimeMillis) (:start-time ctx))]
    {:message-count (count (:messages ctx))
     :token-count (:token-count ctx)
     :estimated-cost (* (:token-count ctx) 0.000001)
     :duration (str (int (/ duration 60000)) " minutes")}))

(defn save-to-file
  "Save context to file"
  [file]
  (io/make-parents file)
  (spit file (pr-str @context)))

(defn load-from-file
  "Load context from file"
  [file]
  (reset! context (edn/read-string (slurp file))))

(defn clear!
  "Clear the context"
  []
  (reset! context {:messages []
                   :token-count 0
                   :start-time (System/currentTimeMillis)}))
