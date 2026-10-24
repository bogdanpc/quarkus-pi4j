#!/usr/bin/env bash
# Show only outdated direct dependencies and plugins. Skips alpha/beta/M/RC versions.
set -uo pipefail
cd "$(dirname "$0")/.."

IGNORE='.*-alpha.*,.*-beta.*,.*-M\d+,.*-RC\d+'

echo "== Dependencies =="
./mvnw versions:display-dependency-updates \
  -DprocessDependencyManagement=false \
  -Dmaven.version.ignore="$IGNORE" 2>&1 \
  | grep -E ' -> ' | sed 's/^\[INFO\] *//' | sort -u \
  | grep . || echo "  all up to date"

echo
echo "== Plugins =="
./mvnw versions:display-plugin-updates \
  -Dmaven.version.ignore="$IGNORE" 2>&1 \
  | sed -n '/The following plugin updates are available/,/^\[INFO\] *$/p' \
  | grep -E ' -> ' | sed 's/^\[INFO\] *//' | sort -u \
  | grep . || echo "  all up to date"
