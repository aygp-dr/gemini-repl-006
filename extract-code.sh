#!/usr/bin/env bash
# Extract all code from the org file

set -euo pipefail

echo "Extracting code from CLOJURE-GEMINI-REPL.org..."

# Create necessary directories
mkdir -p src/gemini_repl/{core,utils,tools}
mkdir -p test/gemini_repl/{core,utils,tools}
mkdir -p scripts
mkdir -p logs
mkdir -p workspace

# Function to extract code between line numbers
extract_code() {
    local start_line="$1"
    local file_path="$2"
    local org_file="CLOJURE-GEMINI-REPL.org"
    
    # Find the end of the code block
    local end_line=$(awk "NR > $start_line && /^#\+end_src/ {print NR; exit}" "$org_file")
    
    if [[ -n "$end_line" ]]; then
        # Extract content between start+1 and end-1
        local content_start=$((start_line + 1))
        local content_end=$((end_line - 1))
        
        # Create directory if needed
        local dir=$(dirname "$file_path")
        mkdir -p "$dir"
        
        # Extract and save
        sed -n "${content_start},${content_end}p" "$org_file" > "$file_path"
        echo "Extracted: $file_path"
    else
        echo "Warning: Could not find end for block starting at line $start_line"
    fi
}

# Extract all tangled files
while IFS=: read -r line_num rest; do
    # Parse the tangle target
    if [[ "$rest" =~ :tangle[[:space:]]+([^[:space:]]+) ]]; then
        target="${BASH_REMATCH[1]}"
        extract_code "$line_num" "$target"
    fi
done < <(grep -n ":tangle" CLOJURE-GEMINI-REPL.org | grep -v ":tangle no")

# Make scripts executable
chmod +x scripts/*.sh 2>/dev/null || true

echo "Code extraction complete!"
echo "Run 'make setup' to complete the setup process."