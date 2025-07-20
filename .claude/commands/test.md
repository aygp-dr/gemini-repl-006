# Test Command

Testing strategies for Gemini REPL development.

## Test Categories

### Unit Tests
- Test individual functions
- Mock external dependencies
- Focus on edge cases

### Integration Tests
- Test component interactions
- Verify API communication
- Test tool execution

### Property-Based Tests
- Use test.check for generative testing
- Test invariants
- Explore edge cases automatically

## Test Organization

```
test/
├── gemini_repl/
│   ├── core/
│   │   ├── repl_test.clj
│   │   └── api_client_test.clj
│   ├── utils/
│   │   ├── context_test.clj
│   │   └── logger_test.clj
│   └── tools/
│       └── system_test.clj
└── integration/
    └── full_flow_test.clj
```

## Running Tests

```bash
# All tests
bb test

# Specific namespace
bb test test.gemini-repl.core.repl-test

# With coverage
bb test:coverage
```
