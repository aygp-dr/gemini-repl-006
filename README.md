# Gemini REPL - Clojure/Babashka Implementation

A fast, interactive REPL for conversations with Google's Gemini AI, implemented in Clojure with Babashka for instant startup.

## Features

- ⚡ Instant startup with Babashka
- 🔧 Tool system for file operations
- 📝 Conversation context management with token tracking
- 📊 Structured logging (file and FIFO)
- 🎯 Self-hosting capabilities
- 🔄 Org-mode literate programming

## Quick Start

```bash
# Clone the repository
git clone https://github.com/aygp-dr/gemini-repl-006.git
cd gemini-repl-006

# Set up environment
make setup

# Configure API key
cp .env.example .env
# Edit .env and add your GEMINI_API_KEY

# Run the REPL
make run
```

## Requirements

- [Babashka](https://babashka.org) (v1.3.0+)
- Gemini API key
- Unix-like environment (macOS, Linux, FreeBSD)

## Development

```bash
# Run tests
make test

# Lint code
make lint

# Start development REPL
make dev

# Build native image (requires GraalVM)
make build
```

## Commands

- `/help` - Show available commands
- `/exit` or `/quit` - Exit the REPL
- `/clear` - Clear the screen
- `/context` - Show conversation history
- `/stats` - Display usage statistics
- `/save [filename]` - Save conversation
- `/load filename` - Load conversation
- `/tools` - List available tools
- `/workspace` - Show workspace contents
- `/debug` - Toggle debug mode

## Architecture

This implementation uses:
- Babashka for fast scripting and REPL
- EDN for data serialization
- HTTP client for Gemini API
- Structured logging with JSON
- Tool system for extensibility

## Evolution

This is version 006 in the gemini-repl series, bringing together:
- Fast startup from Babashka
- Architecture patterns from Python version (005)
- Clojure idioms from original versions (001-003)
- Literate programming with org-mode

## License

MIT License - see LICENSE file
