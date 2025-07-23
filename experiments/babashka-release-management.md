# Experiment: Release Management and Versioning for Babashka Projects

**Status**: Draft  
**Date**: 2025-07-23  
**Researcher**: Observer  

## Objective

Establish automated release management practices for Babashka projects, including:
- Semantic versioning strategy
- Automated changelog generation
- Release artifact creation
- Version bumping workflows
- Deployment coordination

## Background

Babashka projects benefit from fast release cycles due to:
- Quick compilation and testing
- Smaller artifact sizes
- Direct deployment capabilities
- Integration with existing Clojure tooling

## Research Questions

1. **Versioning Strategy**: How to apply semver to Babashka scripts and pods?
2. **Changelog Generation**: What tools work best for Clojure/Babashka projects?
3. **Release Artifacts**: What should be included in releases (JARs, binaries, scripts)?
4. **Automation**: How to integrate version bumping with CI/CD?
5. **Dependencies**: How to manage version compatibility with Babashka itself?

## Semantic Versioning for Babashka

### Version Semantics
- **MAJOR**: Breaking API changes, Babashka version requirements
- **MINOR**: New features, new dependencies, non-breaking API additions  
- **PATCH**: Bug fixes, documentation updates, internal improvements

### Special Considerations
- Babashka version compatibility matrix
- Pod binary compatibility across platforms
- Script compatibility with different shells

## Experimental Design

### Phase 1: Version Management Tools
```bash
# Investigate existing tools
bb deps :deps '{:deps {lein-v/lein-v {:mvn/version "7.4.5"}}}' \
  -e "(require '[lein-v.core :as v]) (v/version)"

# Test semantic version parsing
bb -e "(require '[clojure.string :as str]) 
       (-> \"1.2.3\" (str/split #\"\.\") (->> (map #(Integer/parseInt %))))"
```

### Phase 2: Changelog Automation
- Integration with conventional commits
- Template-based changelog generation
- Release note extraction from git history
- Breaking change detection

### Phase 3: Release Orchestration
```bash
# Example release workflow
#!/bin/bash
set -euo pipefail

# 1. Determine next version
CURRENT_VERSION=$(git describe --tags --abbrev=0)
NEXT_VERSION=$(bb scripts/next-version.clj "$CURRENT_VERSION")

# 2. Update version files
bb scripts/update-version.clj "$NEXT_VERSION"

# 3. Generate changelog
bb scripts/generate-changelog.clj "$CURRENT_VERSION" "$NEXT_VERSION"

# 4. Create release commit
git add -A
git commit -m "chore(release): $NEXT_VERSION"
git tag -a "v$NEXT_VERSION" -m "Release $NEXT_VERSION"

# 5. Build artifacts
gmake gemini-build
gmake gemini-test
gmake gemini-package

# 6. Deploy
git push origin main --tags
bb scripts/deploy.clj "$NEXT_VERSION"
```

### Phase 4: Version Compatibility Matrix
- Babashka version requirements
- JVM compatibility (when needed)
- Platform-specific constraints
- Dependency version tracking

## Tools Evaluation

### Version Management
- **lein-v**: Git-based versioning
- **semantic-release**: Node.js ecosystem tool
- **bump2version**: Python-based version bumping
- **Custom scripts**: Babashka-native solutions

### Changelog Generation
- **conventional-changelog**: Conventional commits parsing
- **git-cliff**: Rust-based changelog generator
- **keep-a-changelog**: Manual changelog format
- **Custom**: Parse git history with Babashka

### Release Automation
- **GitHub Actions**: Native GitHub integration
- **GitLab CI**: Self-hosted option
- **Custom scripts**: Maximum flexibility
- **Clojars deployment**: Maven-based process

## Version File Management

### bb.edn Version Tracking
```clojure
{:version "1.2.3"
 :min-bb-version "1.12.0"
 :paths ["src"]
 :deps {...}}
```

### Version Coordination
- Keep version in single source of truth
- Update all references atomically
- Validate version format and constraints
- Check for version conflicts

## Release Artifact Strategy

### Core Artifacts
- [ ] Source code (Git tag)
- [ ] Uberjar (if applicable)
- [ ] Native binary (if using GraalVM)
- [ ] Pod binaries (platform-specific)
- [ ] Documentation (README, CHANGELOG)

### Distribution Channels
- [ ] GitHub Releases
- [ ] Clojars (for libraries/pods)
- [ ] Package managers (Homebrew, etc.)
- [ ] Container registries (if applicable)

## Expected Artifacts

- [ ] Automated version bumping scripts
- [ ] Changelog generation pipeline
- [ ] Release orchestration workflow
- [ ] Version compatibility documentation
- [ ] Deployment automation
- [ ] Rollback procedures

## Success Criteria

1. One-command release process
2. Automated changelog generation
3. Consistent version tracking across files
4. Compatibility matrix maintenance
5. Rollback capability for failed releases

## Integration with gmake Targets

### Custom Task Names (avoiding Babashka conflicts)
```makefile
gemini-version-bump:
	bb scripts/version-bump.clj $(TYPE)

gemini-changelog:
	bb scripts/generate-changelog.clj

gemini-release:
	bb scripts/release.clj $(VERSION)

gemini-deploy:
	bb scripts/deploy.clj
```

## Conventional Commits Integration

### Commit Message Format
```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

### Version Bump Rules
- `feat:` → MINOR version bump
- `fix:` → PATCH version bump  
- `BREAKING CHANGE:` → MAJOR version bump
- `chore:`, `docs:`, `style:` → No version bump

## Timeline

- **Week 1**: Tool evaluation and version management setup
- **Week 2**: Changelog automation implementation
- **Week 3**: Release orchestration workflow
- **Week 4**: Testing and documentation

## Edge Cases & Considerations

### Pre-release Versions
- Alpha: `1.2.3-alpha.1`
- Beta: `1.2.3-beta.1`  
- Release candidate: `1.2.3-rc.1`

### Hotfix Releases
- Emergency patch process
- Bypass normal release pipeline
- Immediate deployment capability

### Version Conflicts
- Dependency version resolution
- Babashka compatibility matrix
- Platform-specific version tracking

## Related Experiments

- [Babashka Build/Test/Package Workflow](./babashka-build-test-package.md)
- [CI/CD Setup for Babashka Projects](./babashka-cicd-setup.md)
- [Clojars Pod Deployment](./babashka-clojars-deployment.md)

## Risk Assessment

- **High**: Complex dependency version resolution
- **Medium**: Changelog generation accuracy
- **Low**: Basic version bumping automation

## References

- [Semantic Versioning](https://semver.org/)
- [Conventional Commits](https://www.conventionalcommits.org/)  
- [Keep a Changelog](https://keepachangelog.com/)
- [Babashka Book - Project Setup](https://book.babashka.org/#project_setup)