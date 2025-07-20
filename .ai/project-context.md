# Gemini REPL Clojure/Babashka Implementation

This is a Clojure/Babashka implementation of the Gemini REPL, evolved from the Python version (005).

## Key Features
- Instant startup with Babashka
- Functional programming with immutable data
- EDN for data serialization
- Multimethod-based command dispatch
- Tool system with safety constraints
- FIFO and file logging

## Architecture
- Event-driven REPL with slash commands
- Token-aware context management
- Pluggable tool system
- Structured JSON logging
- Native image compilation support

## Development
- Literate programming with org-mode
- All code tangled from CLOJURE-GEMINI-REPL.org
- Test-driven development
- FreeBSD compatible (gmake support)
