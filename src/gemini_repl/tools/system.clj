(ns gemini-repl.tools.system
  "Tool system for file operations and more"
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [babashka.process :as p]))

(def ^:private workspace-dir (or (System/getenv "WORKSPACE_DIR") "workspace"))
(def ^:private enable-self-modify? (= "true" (System/getenv "ENABLE_SELF_MODIFY")))

(defn- safe-path?
  "Check if path is safe (within workspace)"
  [path]
  (let [file (io/file workspace-dir path)
        canonical (.getCanonicalPath file)
        workspace-canonical (.getCanonicalPath (io/file workspace-dir))]
    (str/starts-with? canonical workspace-canonical)))

(defn- read-file
  "Read file contents"
  [{:keys [path]}]
  (if (safe-path? path)
    (let [file (io/file workspace-dir path)]
      (if (.exists file)
        {:success true :content (slurp file)}
        {:success false :error "File not found"}))
    {:success false :error "Path outside workspace"}))

(defn- write-file
  "Write file contents"
  [{:keys [path content]}]
  (if (safe-path? path)
    (let [file (io/file workspace-dir path)]
      (io/make-parents file)
      (spit file content)
      {:success true :message (str "Wrote " (count content) " bytes to " path)})
    {:success false :error "Path outside workspace"}))

(defn- list-files
  "List files in directory"
  [{:keys [path]}]
  (let [path (or path ".")
        dir (io/file workspace-dir path)]
    (if (and (safe-path? path) (.exists dir) (.isDirectory dir))
      {:success true
       :files (mapv (fn [f]
                      {:name (.getName f)
                       :type (if (.isDirectory f) "directory" "file")
                       :size (if (.isFile f) (.length f) nil)})
                    (.listFiles dir))}
      {:success false :error "Invalid directory"})))

(defn- execute-command
  "Execute shell command (restricted)"
  [{:keys [command]}]
  ;; Very restricted for safety
  (if (re-matches #"^(ls|pwd|date|echo).*" command)
    (let [result (p/shell {:out :string :err :string} command)]
      {:success (zero? (:exit result))
       :output (:out result)
       :error (:err result)})
    {:success false :error "Command not allowed"}))

(defn- self-modify
  "Modify own source code"
  [{:keys [file-path content]}]
  (if enable-self-modify?
    (let [file (io/file file-path)]
      (if (.exists file)
        (do
          (spit file content)
          {:success true :message (str "Modified " file-path)})
        {:success false :error "Source file not found"}))
    {:success false :error "Self-modification disabled"}))

(def ^:private tools
  {:read_file {:name "read_file"
               :description "Read contents of a file in the workspace"
               :parameters {:path {:type "string" :description "File path relative to workspace"}}
               :handler read-file}
   
   :write_file {:name "write_file"
                :description "Write contents to a file in the workspace"
                :parameters {:path {:type "string" :description "File path relative to workspace"}
                             :content {:type "string" :description "Content to write"}}
                :handler write-file}
   
   :list_files {:name "list_files"
                :description "List files in a directory"
                :parameters {:path {:type "string" :description "Directory path (optional)"}}
                :handler list-files}
   
   :execute {:name "execute"
             :description "Execute a shell command (limited)"
             :parameters {:command {:type "string" :description "Command to execute"}}
             :handler execute-command}
   
   :self_modify {:name "self_modify"
                 :description "Modify the REPL's own source code"
                 :parameters {:file-path {:type "string" :description "Source file path"}
                              :content {:type "string" :description "New content"}}
                 :handler self-modify}})

(defn get-definitions
  "Get tool definitions for API"
  []
  (mapv (fn [[_ tool]]
          {:name (:name tool)
           :description (:description tool)
           :parameters {:type "object"
                        :properties (:parameters tool)
                        :required (vec (keys (:parameters tool)))}})
        tools))

(defn list-tools
  "List available tools"
  []
  (mapv (fn [[_ tool]]
          {:name (:name tool)
           :description (:description tool)})
        tools))

(defn execute
  "Execute a tool"
  [tool-name args]
  (if-let [tool (get tools (keyword tool-name))]
    (try
      ((:handler tool) args)
      (catch Exception e
        {:success false :error (.getMessage e)}))
    {:success false :error "Unknown tool"}))
