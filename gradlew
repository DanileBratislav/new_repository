#!/usr/bin/env sh
# Lightweight Gradle bootstrapper: downloads Gradle if it is not already cached.
set -eu

GRADLE_VERSION=8.10.2
GRADLE_USER_HOME=${GRADLE_USER_HOME:-"${HOME}/.gradle"}
DIST_DIR="$GRADLE_USER_HOME/wrapper/dists/gradle-${GRADLE_VERSION}-bin/copilot"
GRADLE_HOME="$DIST_DIR/gradle-${GRADLE_VERSION}"
GRADLE_BIN="$GRADLE_HOME/bin/gradle"

if [ ! -x "$GRADLE_BIN" ]; then
    mkdir -p "$DIST_DIR"
    ZIP_FILE="$DIST_DIR/gradle-${GRADLE_VERSION}-bin.zip"
    URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

    if [ ! -f "$ZIP_FILE" ]; then
        if command -v curl >/dev/null 2>&1; then
            curl -fL "$URL" -o "$ZIP_FILE"
        elif command -v wget >/dev/null 2>&1; then
            wget -O "$ZIP_FILE" "$URL"
        else
            echo "Install curl or wget to download Gradle." >&2
            exit 1
        fi
    fi

    if ! command -v unzip >/dev/null 2>&1; then
        echo "Install unzip to extract the Gradle distribution." >&2
        exit 1
    fi
    unzip -q -o "$ZIP_FILE" -d "$DIST_DIR"
fi

exec "$GRADLE_BIN" "$@"
