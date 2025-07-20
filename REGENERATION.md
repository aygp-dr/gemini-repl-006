# Gemini REPL (Clojure) Regeneration Guide

**Project**: Gemini REPL Project  
**Date**: 2025-07-18

## Overview

This document provides instructions for regenerating the Gemini REPL Clojure/Babashka implementation from the literate org-mode source.

## Prerequisites

- Emacs with org-mode (for tangling)
- Babashka 1.3.0+
- Optional: clj-kondo for linting
- Optional: GraalVM for native image builds

## Regeneration Steps

### From Scratch

1. Extract all code from the org file:
   ```bash
   make tangle
   ```

2. Set up the development environment:
   ```bash
   make setup
   ```

3. Configure your API key:
   ```bash
   cp .env.example .env
   # Edit .env and add your GEMINI_API_KEY
   ```

4. Run the REPL:
   ```bash
   make run
   ```

### Development Workflow

1. Edit code in CLOJURE-GEMINI-REPL.org
2. Tangle to extract changes: `make tangle`
3. Test changes: `make test`
4. Run the REPL: `make run`

### Building Native Image

Requires GraalVM with native-image installed:

```bash
make build
./target/gemini-repl
```

## Architecture Notes

### Key Differences from Python Version

1. **Startup Time**: Near-instant with Babashka vs Python startup
2. **Data Format**: EDN instead of JSON for persistence
3. **Concurrency**: Clojure's immutable data structures
4. **Tools**: More functional approach to tool system

### Component Responsibilities

- `core/repl.clj` - Main event loop and command handling
- `core/api-client.clj` - HTTP client for Gemini API
- `utils/context.clj` - Immutable context management
- `utils/logger.clj` - Structured logging
- `tools/system.clj` - Extensible tool framework

## Common Issues

### FIFO Creation
The FIFO is created by direnv. If missing:
```bash
mkfifo logs/gemini.fifo
```

### FreeBSD Compatibility
Use `gmake` instead of `make` on FreeBSD systems.

### API Key Issues
Ensure GEMINI_API_KEY is set in .env file and loaded by direnv.

## Extension Points

1. **New Tools**: Add to `tools/system.clj`
2. **New Commands**: Add method to `handle-command` multimethod
3. **API Models**: Update `model` in `api-client.clj`
4. **Logging**: Extend `logger.clj` for new outputs
