#!/bin/bash

# Script to prepare for next development iteration after release
# Usage: prepare-next-dev.sh <released-version>

set -e

if [ $# -ne 1 ]; then
    echo "Usage: $0 <released-version>"
    echo "Example: $0 1.0.0"
    echo "This will set the next version to 1.0.1-SNAPSHOT"
    exit 1
fi

RELEASED_VERSION="$1"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Calculate next development version
IFS='.' read -ra VERSION_PARTS <<< "$RELEASED_VERSION"
MAJOR=${VERSION_PARTS[0]}
MINOR=${VERSION_PARTS[1]}
PATCH=${VERSION_PARTS[2]}

# Increment patch version for next development
NEXT_PATCH=$((PATCH + 1))
NEXT_DEV_VERSION="$MAJOR.$MINOR.$NEXT_PATCH-SNAPSHOT"

echo "🔄 Preparing for next development iteration..."
echo "Released version: $RELEASED_VERSION"
echo "Next dev version: $NEXT_DEV_VERSION"

# Ensure we're in the project root
cd "$PROJECT_ROOT"

echo "📝 Updating Maven versions to $NEXT_DEV_VERSION..."
./mvnw versions:set -DnewVersion="$NEXT_DEV_VERSION" -DgenerateBackupPoms=false

echo "📝 Updating README.md to show dev version..."
"$SCRIPT_DIR/update-readme-version.sh" "$NEXT_DEV_VERSION"

# Update README status to show it's in development
sed -i.bak 's/| \*\*Status:\*\* Pre-release (not on Maven Central)/| \*\*Status:\*\* Development (not on Maven Central)/' README.md
rm -f README.md.bak

echo "📦 Committing next development version..."
git add -A
git commit -m "Prepare for next development iteration: $NEXT_DEV_VERSION

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"

echo "✅ Ready for next development iteration!"
echo ""
echo "Current version: $NEXT_DEV_VERSION"
echo "Status: Development"
echo ""
echo "Push changes: git push origin main"