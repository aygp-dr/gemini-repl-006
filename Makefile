# Gemini REPL Makefile for Clojure/Babashka
# Supports both GNU make and BSD make

# Detect OS and set make command
UNAME_S := $(shell uname -s)
ifeq ($(UNAME_S),FreeBSD)
    MAKE := gmake
else
    MAKE := make
endif

# Default target
.PHONY: all
all: setup

# Setup development environment
.PHONY: setup
setup:
	@echo "Setting up Gemini REPL (Clojure)..."
	@bash scripts/setup.sh
	@echo "Creating environment file..."
	@cp -n .env.example .env || true
	@echo "✓ Setup complete. Edit .env with your GEMINI_API_KEY"

# Linting
.PHONY: lint
lint:
	@echo "Running linter..."
	@bb lint

# Testing
.PHONY: test
test:
	@echo "Running tests..."
	@bb test

# Quick REPL test
.PHONY: test-repl
test-repl:
	@echo "Testing REPL functionality..."
	@bash scripts/test-repl.sh

# Build native image
.PHONY: build
build:
	@echo "Building native image..."
	@bb build

# Run the REPL
.PHONY: run
run:
	@echo "Starting Gemini REPL..."
	@bb run

# Run REPL in tmux with timeout
.PHONY: tmux-repl
tmux-repl:
	@echo "Starting REPL in tmux session 'gemini-repl'..."
	@tmux new-session -d -s gemini-repl "timeout 30m bb run" || tmux attach -t gemini-repl
	@echo "Attach with: tmux attach -t gemini-repl"

# Development mode
.PHONY: dev
dev:
	@echo "Starting development REPL..."
	@bb dev

# Clean build artifacts
.PHONY: clean
clean:
	@bb clean

# Tangle org files
.PHONY: tangle
tangle:
	@echo "Tangling CLOJURE-GEMINI-REPL.org..."
	@emacs --batch -l org --eval '(org-babel-tangle-file "CLOJURE-GEMINI-REPL.org")'
	@echo "✓ Tangle complete"

# Detangle back to org
.PHONY: detangle
detangle:
	@echo "Detangling back to org files..."
	@emacs --batch -l org --eval '(org-babel-detangle "CLOJURE-GEMINI-REPL.org")'
	@echo "✓ Detangle complete"

# Watch FIFO logs
.PHONY: watch-logs
watch-logs:
	@echo "Watching FIFO logs..."
	@cat logs/gemini.fifo

# Help
.PHONY: help
help:
	@echo "Gemini REPL - Available targets:"
	@echo "  make setup      - Set up development environment"
	@echo "  make lint       - Run code linter"
	@echo "  make test       - Run tests"
	@echo "  make build      - Build native image"
	@echo "  make run        - Run the REPL"
	@echo "  make dev        - Start development REPL"
	@echo "  make clean      - Clean build artifacts"
	@echo "  make tangle     - Extract code from org file"
	@echo "  make detangle   - Update org file from code"
	@echo "  make watch-logs - Watch FIFO logs"

# Check prerequisites
.PHONY: check
check:
	@echo "Checking prerequisites..."
	@command -v bb >/dev/null 2>&1 || { echo "❌ Babashka not found. Install from https://babashka.org"; exit 1; }
	@command -v clj-kondo >/dev/null 2>&1 || echo "⚠️  clj-kondo not found. Install for linting support"
	@echo "✓ Prerequisites check complete"
