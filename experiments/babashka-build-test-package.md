# Experiment: Babashka Project Build/Test/Package Workflow

**Status**: Draft  
**Date**: 2025-07-23  
**Researcher**: Observer  

## Objective

Establish best practices for building, testing, and packaging Babashka projects, with focus on:
- Project structure and dependencies (`bb.edn`)
- Testing frameworks and conventions
- Packaging for distribution (JARs, native images)
- Pod creation and deployment strategies

## Background

Babashka projects differ from traditional Clojure projects in several ways:
- Fast startup times vs JVM Clojure
- Limited library ecosystem but growing pod support
- Native binary compilation options
- Integration with shell/system tools

## Research Questions

1. **Build System**: What are the standard practices for `bb.edn` configuration?
2. **Testing**: Which testing frameworks work best with Babashka? (clojure.test, test-check, etc.)
3. **Packaging**: When to use JAR vs native-image vs script distribution?
4. **Pod Architecture**: How do Babashka pods differ from regular libraries?
5. **Dependency Management**: How to handle git deps vs Maven deps vs local deps?

## Experimental Design

### Phase 1: Minimal Project Setup
- Create `bb.edn` with essential configuration
- Set up basic project structure following conventions
- Implement hello-world functionality

### Phase 2: Testing Integration
- Add `clojure.test` based test suite
- Explore property-based testing with `test.check`
- Set up test runner and reporting
- Investigate test coverage tools

### Phase 3: Build Variants
- Standard script execution (`bb script.clj`)
- JAR packaging (`bb uberjar`)
- Native compilation with GraalVM
- Performance comparison between variants

### Phase 4: Pod Development
- Create minimal pod with native dependencies
- Test pod loading and unloading
- Document pod API design patterns
- Evaluate pod vs library trade-offs

## Expected Artifacts

- [ ] `bb.edn` template with best practices
- [ ] Test suite demonstrating framework integration
- [ ] Build scripts for multiple packaging options
- [ ] Performance benchmarks for different deployment strategies
- [ ] Pod development template
- [ ] Documentation of conventions and anti-patterns

## Success Criteria

1. Reproducible build process documented
2. Test suite with >80% coverage
3. Successful packaging in multiple formats
4. Working pod example with clear API
5. Performance metrics comparing deployment options

## Dependencies

- Babashka 1.12.206+
- GraalVM (for native compilation)
- GitHub Actions (for CI/CD integration)
- Clojars account (for pod distribution)

## Timeline

- **Week 1**: Project structure and basic testing
- **Week 2**: Build system and packaging experiments  
- **Week 3**: Pod development and deployment
- **Week 4**: Documentation and benchmarking

## Related Experiments

- [CI/CD Setup for Babashka Projects](./babashka-cicd-setup.md)
- [Release Management and Versioning](./babashka-release-management.md)
- [Clojars Pod Deployment](./babashka-clojars-deployment.md)

## Notes

This experiment builds on the literate programming approach used in the main gemini-repl project but focuses on production deployment concerns rather than educational extraction.

Key areas for investigation:
- Startup time optimization
- Memory usage patterns
- Cross-platform compatibility
- Integration with existing Clojure tooling

## References

- [Babashka Book](https://book.babashka.org/)
- [Pod Registry](https://github.com/babashka/pod-registry)
- [bb.edn Reference](https://book.babashka.org/#project_setup)