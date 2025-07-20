#!/usr/bin/env bash
set -euo pipefail

source "$(dirname "$0")/common.sh"

log_info "Building native image with GraalVM..."

# Check for GraalVM
if ! command_exists native-image; then
    log_error "native-image not found. Please install GraalVM."
    exit 1
fi

# Create target directory
mkdir -p target

# Build uberjar with Babashka
log_info "Creating uberjar..."
bb uberjar target/gemini-repl.jar -m gemini-repl.main

# Build native image
log_info "Building native image..."
native-image \
    -jar target/gemini-repl.jar \
    -H:Name=target/gemini-repl \
    -H:+ReportExceptionStackTraces \
    --no-fallback \
    --enable-url-protocols=https \
    --features=clj_easy.graal_build_time.InitClojureClasses

log_info "✓ Native image built: target/gemini-repl"
