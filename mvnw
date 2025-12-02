#!/bin/sh
# Maven Wrapper script
# Downloads and runs Maven if not installed

set -e

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
MAVEN_OPTS="${MAVEN_OPTS:--Xmx1024m}"

# Check if Maven is available
if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
fi

# Use IntelliJ's bundled Maven if available
IDEA_MAVEN=$(find ~/.local/share/JetBrains -name "maven3" -type d 2>/dev/null | head -1)
if [ -n "$IDEA_MAVEN" ] && [ -f "$IDEA_MAVEN/bin/mvn" ]; then
    exec "$IDEA_MAVEN/bin/mvn" "$@"
fi

# Fallback: download Maven
MAVEN_VERSION="3.9.6"
MAVEN_HOME="$HOME/.m2/wrapper/dists/apache-maven-$MAVEN_VERSION"

if [ ! -f "$MAVEN_HOME/bin/mvn" ]; then
    echo "Downloading Maven $MAVEN_VERSION..."
    mkdir -p "$MAVEN_HOME"
    curl -fsSL "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.tar.gz" | tar xz -C "$MAVEN_HOME" --strip-components=1
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
