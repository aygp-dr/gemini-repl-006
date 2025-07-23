# Bug Report: JSON Library Compatibility Issue

## Summary
REPL fails to start due to JSON library incompatibility between source code and Babashka runtime environment.

## Environment
- **Babashka Version**: v1.12.206
- **OS**: FreeBSD 14.3-RELEASE
- **Project**: gemini-repl-006

## Error Details

### Error Message
```
Type:     java.io.FileNotFoundException
Message:  Could not locate babashka/json.bb, babashka/json.clj or babashka/json.cljc on classpath.
Location: /home/jwalsh/projects/aygp-dr/gemini-repl-006/src/gemini_repl/core/api_client.clj:3:3
```

### Stack Trace Location
```clojure
(:require [babashka.http-client :as http]
          [babashka.json :as json]  ; <-- This line fails
          [clojure.string :as str])
```

## Root Cause Analysis

### Issue 1: Incorrect JSON Library Reference
- **Problem**: Code references `babashka.json` which doesn't exist
- **Expected**: Babashka should have built-in JSON support
- **Actual**: Library not found on classpath

### Issue 2: Library Mismatch in Codebase
- **Original**: Used `cheshire.core` (not available in Babashka)
- **Attempted Fix**: Changed to `babashka.json` (also not available)
- **Need**: Find correct JSON library for Babashka

## Impact
- **Severity**: Critical - REPL cannot start
- **User Experience**: Complete failure, no functionality available
- **Workaround**: None currently

## Investigation Needed
1. What JSON libraries are actually available in Babashka v1.12.206?
2. How should JSON parsing be handled in this version?
3. Are there built-in JSON functions that should be used instead?

## Reproduction Steps
1. Clone gemini-repl-006 repository
2. Set up environment with Babashka v1.12.206
3. Configure .env with valid GEMINI_API_KEY
4. Run `gmake run`
5. Observe FileNotFoundException

## Expected Behavior
REPL should start successfully and be able to parse JSON responses from Gemini API.

## Proposed Solution
Need to identify the correct JSON library or built-in functions available in Babashka for:
- `json/parse-string` - parsing API responses
- `json/write-str` - serializing API requests

## Status
- **Priority**: High
- **Assigned**: Builder
- **Created**: 2025-07-23
- **Status**: Under Investigation