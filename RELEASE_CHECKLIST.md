# Release Checklist

This checklist helps ensure all version references are updated when releasing a new version.

## Version Update Locations

When releasing a new version, update the version number in these locations:

### 1. Maven POMs
- [ ] `/pom.xml` - Line ~22: `<version>X.Y.Z</version>`
- [ ] `/runtime/pom.xml` - Parent version
- [ ] `/deployment/pom.xml` - Parent version  
- [ ] `/examples/pom.xml` - Parent version
- [ ] `/examples/simple-app/pom.xml` - Parent version and dependency version
- [ ] `/integration-tests/pom.xml` - Parent version

### 2. Documentation (Automated)
- [x] `/README.md` - Version banner automatically updated by Maven replacer plugin
- [x] `/README.md` - `@project.version@` placeholders automatically replaced
- [ ] `/docs/modules/ROOT/pages/includes/attributes.adoc` - Update version attribute (if exists)

### 3. Extension Metadata
- [ ] `/runtime/src/main/resources/META-INF/quarkus-extension.yaml` - Check if version needs update

### 4. Git Operations
- [ ] Create git tag: `git tag vX.Y.Z`
- [ ] Push tag: `git push origin vX.Y.Z`

## Release Process

1. **Update Version Numbers**
   ```bash
   # Use Maven versions plugin to update all POMs
   ./mvnw versions:set -DnewVersion=X.Y.Z
   ./mvnw versions:commit
   ```

2. **Auto-Update Documentation** 
   ```bash
   # Use the automated script to update all version references in README
   ./scripts/update-readme-version.sh X.Y.Z
   ```

3. **Build and Test**
   ```bash
   ./mvnw clean install
   ./mvnw verify -Pit
   ```

4. **Commit Changes**
   ```bash
   git add -A
   git commit -m "Release version X.Y.Z"
   ```

5. **Tag Release**
   ```bash
   git tag -a vX.Y.Z -m "Release version X.Y.Z"
   git push origin main
   git push origin vX.Y.Z
   ```

6. **Deploy to Maven Central** (when configured)
   ```bash
   ./mvnw clean deploy -Prelease
   ```

7. **Update to Next Development Version**
   ```bash
   ./mvnw versions:set -DnewVersion=X.Y.Z-SNAPSHOT
   ./mvnw versions:commit
   git commit -am "Prepare for next development iteration"
   git push origin main
   ```

## Version Numbering Guidelines

- **Major version (X)**: Breaking API changes
- **Minor version (Y)**: New features, backward compatible
- **Patch version (Z)**: Bug fixes, backward compatible

## Notes for Future Maven Central Publishing

When ready to publish to Maven Central:
1. Remove "Pre-release" status from README
2. Update installation instructions to remove "build from source" requirement
3. Add Maven Central badge to README
4. Update Quick Start to use published artifact