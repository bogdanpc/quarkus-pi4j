#!/bin/bash

# Automated release script for Quarkus Pi4J extension
# Usage: release.sh <new-version>

set -e

if [ $# -ne 1 ]; then
    echo "Usage: $0 <version>"
    echo "Example: $0 1.0.0"
    exit 1
fi

NEW_VERSION="$1"
CURRENT_DIR=$(pwd)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "🚀 Starting release process for version $NEW_VERSION..."

# Ensure we're in the project root
cd "$PROJECT_ROOT"

# Check if we're on the main branch
CURRENT_BRANCH=$(git branch --show-current)
if [ "$CURRENT_BRANCH" != "main" ]; then
    echo "⚠️  Warning: You're not on the main branch (currently on: $CURRENT_BRANCH)"
    read -p "Continue anyway? (y/N): " -r
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "❌ Release cancelled"
        exit 1
    fi
fi

# Check for uncommitted changes
if ! git diff --quiet || ! git diff --cached --quiet; then
    echo "❌ Error: You have uncommitted changes. Please commit or stash them first."
    git status --short
    exit 1
fi

echo "📝 Step 1: Updating Maven versions..."
./mvnw versions:set -DnewVersion="$NEW_VERSION" -DgenerateBackupPoms=false

echo "📝 Step 2: Updating README.md version references..."
"$SCRIPT_DIR/update-readme-version.sh" "$NEW_VERSION"

echo "🔨 Step 3: Building and testing..."
./mvnw clean install

echo "🧪 Step 4: Running integration tests..."
if ./mvnw verify -Pit; then
    echo "✅ All tests passed!"
else
    echo "❌ Tests failed! Rolling back changes..."
    git checkout -- .
    exit 1
fi

echo "📦 Step 5: Creating git commit and tag..."
git add -A
git commit -m "Release version $NEW_VERSION"

git tag -a "v$NEW_VERSION" -m "Release version $NEW_VERSION"

echo "🎉 Step 6: Release completed successfully!"
echo ""
echo "✅ Version updated to: $NEW_VERSION"
echo "✅ README.md updated"
echo "✅ Tests passed"
echo "✅ Git commit and tag created"
echo ""
echo "Next steps:"
echo "1. Push changes: git push origin main"
echo "2. Push tag: git push origin v$NEW_VERSION"
echo "3. Create GitHub release (optional)"
echo "4. Deploy to Maven Central (when ready)"
echo ""
echo "To prepare for next development iteration:"
echo "./scripts/prepare-next-dev.sh $NEW_VERSION"

# Return to original directory
cd "$CURRENT_DIR"