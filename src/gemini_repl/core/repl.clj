(ns gemini-repl.core.repl
  "Core REPL implementation with event loop"
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [gemini-repl.core.api-client :as api]
            [gemini-repl.utils.context :as ctx]
            [gemini-repl.utils.logger :as log]
            [gemini-repl.tools.system :as tools]))

(def ^:private running? (atom true))

(defn- display-banner
  "Display the REPL banner"
  []
  (println "
╔══════════════════════════════════════╗
║      🌟 Gemini REPL v1.0-bb 🌟      ║
║   Clojure-powered AI conversations   ║
║   Type /help for available commands  ║
╚══════════════════════════════════════╝"))

(defn- get-prompt
  "Generate the prompt string"
  []
  "> ")

(defn- display-response
  "Display response with metadata"
  [response]
  (println "\n" (:text response))
  (when-let [metadata (:metadata response)]
    (let [{:keys [tokens cost time]} metadata
          indicator (cond
                      (< tokens 1000) "🟢"
                      (< tokens 5000) "🟡"
                      :else "🔴")]
      (printf "\n[%s %d tokens | $%.4f | %.1fs]\n" 
              indicator tokens cost time))))

;; Command handlers
(defmulti handle-command
  "Handle slash commands"
  (fn [cmd _args] (keyword (subs cmd 1))))

(defmethod handle-command :help
  [_ _]
  (println "
Available Commands:
  /help         - Show this help message
  /exit, /quit  - Exit the REPL
  /clear        - Clear the screen
  /context      - Show conversation context
  /stats        - Show usage statistics
  /save [file]  - Save conversation to file
  /load [file]  - Load conversation from file
  /tools        - List available tools
  /workspace    - Show workspace contents
  /debug        - Toggle debug mode

Tool Functions:
  The AI can read, write, and modify files in the workspace directory.
  Ask it to create, edit, or analyze files for you."))

(defmethod handle-command :exit
  [_ _]
  (println "\nGoodbye! 👋")
  (reset! running? false))

(defmethod handle-command :quit
  [_ _]
  (handle-command "/exit" nil))

(defmethod handle-command :clear
  [_ _]
  (print "\033[H\033[2J")
  (flush)
  (display-banner))

(defmethod handle-command :context
  [_ _]
  (let [messages (ctx/get-messages)]
    (println "\n=== Conversation Context ===")
    (doseq [msg (take-last 10 messages)]
      (let [role (str/upper-case (name (:role msg)))
            content (let [c (:content msg)]
                      (if (> (count c) 100)
                        (str (subs c 0 100) "...")
                        c))]
        (println (str role ": " content))))
    (println (str "\nTotal messages: " (count messages)))
    (println (str "Total tokens: " (ctx/get-token-count)))))

(defmethod handle-command :stats
  [_ _]
  (let [stats (ctx/get-stats)]
    (println "\n=== Usage Statistics ===")
    (println (str "Messages: " (:message-count stats)))
    (println (str "Tokens: " (:token-count stats)))
    (printf "Estimated cost: $%.4f\n" (:estimated-cost stats))
    (println (str "Session duration: " (:duration stats)))))

(defmethod handle-command :save
  [_ args]
  (let [filename (or (not-empty args)
                     (str "conversation_" 
                          (.format (java.time.LocalDateTime/now)
                                   (java.time.format.DateTimeFormatter/ofPattern 
                                    "yyyyMMdd_HHmmss"))
                          ".edn"))
        path (io/file "workspace" filename)]
    (ctx/save-to-file path)
    (println (str "Conversation saved to: " path))))

(defmethod handle-command :load
  [_ args]
  (if (empty? args)
    (println "Usage: /load <filename>")
    (let [path (io/file "workspace" args)]
      (if (.exists path)
        (do
          (ctx/load-from-file path)
          (println (str "Conversation loaded from: " path)))
        (println (str "File not found: " path))))))

(defmethod handle-command :tools
  [_ _]
  (println "\n=== Available Tools ===")
  (doseq [tool (tools/list-tools)]
    (println (str "- " (:name tool) ": " (:description tool)))))

(defmethod handle-command :workspace
  [_ _]
  (let [workspace (io/file "workspace")]
    (if (.exists workspace)
      (do
        (println "\n=== Workspace Contents ===")
        (doseq [file (sort (.listFiles workspace))]
          (let [icon (if (.isFile file) "📄" "📁")
                name (.getName file)
                size (if (.isFile file) 
                       (.length file)
                       "-")]
            (printf "%s %-30s %10s\n" icon name size))))
      (println "Workspace directory does not exist"))))

(defmethod handle-command :debug
  [_ _]
  (let [new-level (if (= (log/get-level) :debug) :info :debug)]
    (log/set-level! new-level)
    (println (str "Debug mode: " (if (= new-level :debug) "ON" "OFF")))))

(defmethod handle-command :default
  [cmd _]
  (println (str "Unknown command: " cmd))
  (println "Type /help for available commands"))

(defn- process-input
  "Process user input"
  [input]
  (log/debug {:event :user-input :input input})
  (cond
    (str/starts-with? input "/")
    (let [[cmd & args] (str/split input #"\s+" 2)]
      (handle-command cmd (first args)))
    
    :else
    (try
      ;; Add to context
      (ctx/add-message :user input)
      
      ;; Get API response with tools
      (let [messages (ctx/get-messages)
            tools (tools/get-definitions)
            response (api/send-message messages tools)]
        
        ;; Handle tool calls if present
        (when-let [tool-calls (:tool-calls response)]
          (doseq [call tool-calls]
            (let [result (tools/execute (:name call) (:args call))]
              (ctx/add-tool-response (:name call) result))))
        
        ;; Add response to context
        (ctx/add-message :assistant (:text response))
        
        ;; Display response
        (display-response response))
      
      (catch Exception e
        (log/error {:event :api-error :error (.getMessage e)})
        (println (str "Error: " (.getMessage e)))))))

(defn- load-history
  "Load command history"
  []
  ;; Babashka doesn't have built-in readline support
  ;; This is a placeholder for future enhancement
  nil)

(defn- save-history
  "Save command history"
  []
  ;; Placeholder for future enhancement
  nil)

(defn start!
  "Start the REPL event loop"
  []
  (display-banner)
  (log/info {:event :repl-started})
  (load-history)
  
  (while @running?
    (try
      (print (get-prompt))
      (flush)
      (when-let [input (read-line)]
        (when (not-empty (str/trim input))
          (process-input (str/trim input))))
      (catch Exception e
        (if (instance? java.io.EOFException e)
          (handle-command "/exit" nil)
          (do
            (log/error {:event :repl-error :error (.getMessage e)})
            (println (str "Error: " (.getMessage e))))))))
  
  (save-history)
  (log/info {:event :repl-stopped}))
