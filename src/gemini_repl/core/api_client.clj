(ns gemini-repl.core.api-client
  "Gemini API client implementation"
  (:require [babashka.http-client :as http]
            [babashka.json :as json]
            [clojure.string :as str]))

(def ^:private api-key (System/getenv "GEMINI_API_KEY"))
(def ^:private model (or (System/getenv "GEMINI_MODEL") "gemini-2.0-flash-exp"))
(def ^:private base-url "https://generativelanguage.googleapis.com/v1beta")

(defn- make-request-body
  "Create request body for Gemini API"
  [messages tools]
  (cond-> {:contents (mapv (fn [msg]
                              {:role (name (:role msg))
                               :parts [{:text (:content msg)}]})
                            messages)}
    (seq tools) (assoc :tools [{:function_declarations tools}])))

(defn- parse-response
  "Parse Gemini API response"
  [response]
  (let [body (json/parse-string (:body response) {:key-fn keyword})
        candidate (first (:candidates body))
        content (get-in candidate [:content :parts])
        text-parts (filter #(contains? % :text) content)
        tool-parts (filter #(contains? % :functionCall) content)]
    {:text (str/join "\n" (map :text text-parts))
     :tool-calls (mapv (fn [part]
                         {:name (get-in part [:functionCall :name])
                          :args (get-in part [:functionCall :args])})
                       tool-parts)
     :metadata {:tokens (get-in body [:usageMetadata :totalTokenCount] 0)
                :cost (* (get-in body [:usageMetadata :totalTokenCount] 0) 0.000001)
                :time 0.5}})) ; Placeholder for actual timing

(defn send-message
  "Send message to Gemini API"
  [messages tools]
  (when-not api-key
    (throw (ex-info "GEMINI_API_KEY not set" {})))
  
  (let [url (str base-url "/models/" model ":generateContent?key=" api-key)
        body (make-request-body messages tools)
        response (http/post url
                            {:headers {"Content-Type" "application/json"}
                             :body (json/write-str body)})]
    (if (= 200 (:status response))
      (parse-response response)
      (throw (ex-info "API request failed" 
                      {:status (:status response)
                       :body (:body response)})))))
