#!/bin/bash

# Script to automatically update version references in README.md
# Usage: update-readme-version.sh <new-version>

set -e

if [ $# -ne 1 ]; then
    echo "Usage: $0 <version>"
    echo "Example: $0 1.0.0"
    exit 1
fi

NEW_VERSION="$1"
README_FILE="README.md"

# Check if README exists
if [ ! -f "$README_FILE" ]; then
    echo "Error: $README_FILE not found"
    exit 1
fi

echo "Updating README.md version references to $NEW_VERSION..."

# Update the version banner at the top of README
sed -i.bak "s/\*\*Current Version:\*\* \`[^\`]*\`/\*\*Current Version:\*\* \`$NEW_VERSION\`/g" "$README_FILE"

# Update any placeholder version references
sed -i.bak "s/@project\.version@/$NEW_VERSION/g" "$README_FILE"

# Update hardcoded version references in dependency examples (escape < and > properly)
sed -i.bak "s/\<version\>[^<]*\<\/version\>/\<version\>$NEW_VERSION\<\/version\>/g" "$README_FILE"

# Update Gradle implementation versions
sed -i.bak "s/implementation 'io\.github\.bogdanpc:quarkus-pi4j:[^']*'/implementation 'io.github.bogdanpc:quarkus-pi4j:$NEW_VERSION'/g" "$README_FILE"

# Update the "Current version" references (lowercase)
sed -i.bak "s/\*\*Current version:\*\* \`[^\`]*\`/\*\*Current version:\*\* \`$NEW_VERSION\`/g" "$README_FILE"

# Remove backup file
rm -f "$README_FILE.bak"

echo "✅ README.md updated successfully with version $NEW_VERSION"

# Show what changed
echo "Updated lines:"
grep -n "$NEW_VERSION" "$README_FILE" || echo "No version references found (this might be intentional)"