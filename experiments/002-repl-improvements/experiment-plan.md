# Experiment 002: REPL Improvements

## Objective
Fix critical REPL usability issues: missing response display, complex prompts, and JSON library compatibility.

## Background
The Gemini REPL launched successfully but had three major issues:
1. Gemini responses not displaying (only token counts shown)
2. Complex prompt format `[X tokens] >` instead of simple `>`
3. JSON library mismatch between babashka and cheshire dependencies

## Hypothesis
The response display issue is caused by JSON parsing failures due to library incompatibility, preventing proper response extraction from Gemini API.

## Methodology

### Phase 1: Investigation ✅
- Analyzed REPL implementation in core/repl.clj
- Identified JSON library mismatch in api_client.clj
- Found complex prompt generation logic

### Phase 2: Implementation ✅
- Fixed JSON library: cheshire.core → babashka.json
- Simplified prompt: removed token display, just `> `
- Updated JSON parsing calls for babashka compatibility

### Phase 3: Testing (IN PROGRESS)
- Test basic REPL startup
- Verify response display with simple queries
- Check JSON parsing with Gemini API responses

## Results So Far

### Fixed Issues:
1. ✅ Prompt simplified to `> `
2. ✅ JSON library aligned with babashka
3. ✅ API parsing methods updated

### Current Issue:
- JSON library `babashka.json` not available in Babashka runtime
- Error: `Could not locate babashka/json.bb, babashka/json.clj or babashka/json.cljc on classpath`

## Next Steps
1. Investigate available JSON libraries in Babashka
2. Find correct JSON parsing approach for Babashka
3. Test end-to-end response display
4. Document final solution

## Files Modified
- `src/gemini_repl/core/api_client.clj` - JSON library change
- `src/gemini_repl/core/repl.clj` - Prompt simplification

## Test Environment
- Babashka v1.12.206
- FreeBSD 14.3-RELEASE
- Gemini API key configured