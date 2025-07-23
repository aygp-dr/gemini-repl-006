# Experiment: CI/CD Setup for Babashka Projects

**Status**: Draft  
**Date**: 2025-07-23  
**Researcher**: Observer  

## Objective

Design and implement CI/CD pipelines specifically for Babashka projects, addressing:
- Multi-platform testing (Linux, macOS, Windows, FreeBSD)
- Pod compilation and testing
- Performance regression detection
- Automated deployment workflows

## Background

Babashka projects have unique CI/CD requirements:
- Fast startup times enable different testing strategies
- Native compilation requires platform-specific runners
- Pod development needs native library management
- Integration with existing Clojure toolchains

## Research Questions

1. **Platform Coverage**: Which GitHub Actions runners support Babashka?
2. **Test Strategies**: How to structure tests for maximum feedback speed?
3. **Pod CI**: How to build and test pods in CI environments?
4. **Performance**: How to detect performance regressions in startup/execution?
5. **Integration**: How to integrate with existing Clojure tooling (deps, lein)?

## Experimental Design

### Phase 1: Basic CI Pipeline
```yaml
# .github/workflows/ci.yml
name: CI
on: [push, pull_request]
jobs:
  test:
    matrix:
      os: [ubuntu-latest, macos-latest, windows-latest]
    runs-on: ${{ matrix.os }}
    steps:
      - uses: actions/checkout@v4
      - name: Install Babashka
        uses: turtlequeue/setup-babashka@v1.5.0
      - name: Run tests
        run: bb test
```

### Phase 2: Performance Monitoring
- Startup time benchmarks
- Memory usage tracking  
- Execution time regression detection
- Historical performance data

### Phase 3: Pod-Specific CI
- Native library compilation
- Cross-platform binary testing
- Pod loading verification
- Integration test suites

### Phase 4: Deployment Automation
- Automated version bumping
- Changelog generation
- Clojars deployment
- GitHub releases

## Platform-Specific Considerations

### Linux (ubuntu-latest)
- Standard Babashka installation
- Native library compilation
- Container testing support

### macOS (macos-latest)
- Intel and ARM64 support
- Code signing for native binaries
- Homebrew integration testing

### Windows (windows-latest)
- PowerShell vs CMD compatibility
- Windows-specific path handling
- .exe binary generation

### FreeBSD (self-hosted or custom)
- Limited runner availability
- Custom setup requirements
- Unix compatibility testing

## Test Organization Strategy

### Fast Feedback Loop
```bash
# Unit tests (< 5 seconds)
bb test:unit

# Integration tests (< 30 seconds)  
bb test:integration

# Performance tests (< 2 minutes)
bb test:performance

# Full test suite
bb test:all
```

### Parallel Execution
- Split tests by namespace
- Run independent test suites in parallel
- Optimize for CI runner limitations

## Performance Benchmarking Framework

### Startup Time Tracking
```bash
#!/bin/bash
# benchmark-startup.sh
for i in {1..10}; do
    time bb -e "(println \"Hello World\")" 2>&1 | grep real
done | awk '{sum+=$2} END {print "Average:", sum/NR}'
```

### Memory Usage Monitoring
```bash
# Use GNU time or similar to track memory
/usr/bin/time -v bb your-script.clj 2>&1 | grep "Maximum resident"
```

## Expected Artifacts

- [ ] Multi-platform GitHub Actions workflow
- [ ] Performance regression detection system
- [ ] Pod-specific CI configuration
- [ ] Automated deployment pipeline
- [ ] Test organization best practices
- [ ] Performance benchmarking tools

## Success Criteria

1. Sub-5-minute CI pipeline for basic tests
2. Cross-platform compatibility verified
3. Performance regression detection working
4. Automated deployment to Clojars
5. Pod compilation tested on all platforms

## Integration Points

### With Existing Tools
- **make**: Use gmake targets for consistent interface
- **git**: Integrate with git hooks and branch protection
- **Clojars**: Automated deployment on version tags
- **GitHub**: Issues, releases, and project boards

### Naming Conventions (gemini-repl prefixed)
- `gemini-test` instead of `bb test`
- `gemini-build` instead of `bb uberjar`
- `gemini-run` instead of `bb run`
- Avoids Babashka built-in command conflicts

## Timeline

- **Week 1**: Basic CI pipeline setup and testing
- **Week 2**: Performance monitoring implementation
- **Week 3**: Pod-specific CI development
- **Week 4**: Deployment automation and documentation

## Known Issues & Solutions

### Babashka Task Override Warning
```
[babashka] WARNING: task(s) 'run', 'repl' override built-in command(s). 
Use :override-builtin true to disable warning.
```

**Solution**: Use prefixed task names like `gemini-run`, `gemini-repl`

### Timeout Testing
```bash
# Test with timeout for CI environments
timeout 5 gmake gemini-run
```

## Related Experiments

- [Babashka Build/Test/Package Workflow](./babashka-build-test-package.md)
- [Release Management and Versioning](./babashka-release-management.md)
- [Clojars Pod Deployment](./babashka-clojars-deployment.md)

## Risk Assessment

- **High**: Windows compatibility and PowerShell issues
- **Medium**: Performance regression detection accuracy
- **Low**: Basic CI pipeline setup

## References

- [GitHub Actions for Clojure](https://github.com/marketplace/actions/setup-clojure)
- [Babashka CI Examples](https://github.com/babashka/babashka/tree/master/.github/workflows)
- [Performance Testing Best Practices](https://github.com/google/benchmark)