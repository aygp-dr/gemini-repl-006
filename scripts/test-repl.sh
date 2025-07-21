#!/usr/bin/env bash
# Test the REPL without blocking

set -euo pipefail

echo "=== Testing Gemini REPL ==="

# Method 1: Using tmux with detached session
echo "1. Starting REPL in tmux session..."
tmux new-session -d -s test-repl "timeout 30 bb run"
sleep 2

# Check if session is running
if tmux has-session -t test-repl 2>/dev/null; then
    echo "✓ REPL started in tmux session 'test-repl'"
    
    # Send test command to REPL
    echo "2. Sending test command to REPL..."
    tmux send-keys -t test-repl "/help" C-m
    sleep 1
    
    # Capture output
    tmux capture-pane -t test-repl -p | tail -20
    
    # Send exit command
    tmux send-keys -t test-repl "/exit" C-m
    sleep 1
    
    # Kill session
    tmux kill-session -t test-repl 2>/dev/null || true
else
    echo "✗ Failed to start REPL in tmux"
fi

echo ""
echo "=== Alternative test using expect ==="

# Method 2: Using expect for automated interaction
cat > /tmp/test-repl.expect << 'EOF'
#!/usr/bin/expect -f

set timeout 5
spawn timeout 10 bb run

expect {
    "tokens] >" {
        send "/help\r"
        expect "*Available commands*"
        send "/exit\r"
        expect eof
        exit 0
    }
    timeout {
        puts "Timeout waiting for REPL prompt"
        exit 1
    }
}
EOF

if command -v expect >/dev/null 2>&1; then
    echo "3. Testing with expect..."
    expect /tmp/test-repl.expect
else
    echo "⚠️  expect not found, skipping expect test"
fi

echo ""
echo "=== Quick socket test ==="

# Method 3: Start REPL with input from pipe
echo "4. Testing with pipe input..."
(echo "/help"; echo "/exit") | timeout 5 bb run 2>&1 | grep -E "(Available commands|Goodbye)" && echo "✓ Pipe test passed"

echo ""
echo "=== Non-blocking background test ==="

# Method 4: Background process with timeout
echo "5. Starting REPL in background with timeout..."
timeout 10 bb run > /tmp/repl-output.log 2>&1 &
REPL_PID=$!
sleep 2

if ps -p $REPL_PID > /dev/null 2>&1; then
    echo "✓ REPL running with PID $REPL_PID"
    kill $REPL_PID 2>/dev/null || true
else
    echo "✗ REPL process not found"
fi

# Clean up
rm -f /tmp/test-repl.expect /tmp/repl-output.log

echo ""
echo "=== Test complete ==="