#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

DEPENDENCY_CMD=(./gradlew dependencies)
VERIFY_CMD=(./gradlew build lint)
START_CMD=(./gradlew installDebug)

echo "==> Working directory: $PWD"

if [[ ! -x "./gradlew" ]]; then
    echo "ERROR: ./gradlew is missing or is not executable."
    echo "Run: chmod +x ./gradlew"
    exit 1
fi

echo "==> Resolving Gradle dependencies"
"${DEPENDENCY_CMD[@]}"

echo "==> Running baseline verification"
"${VERIFY_CMD[@]}"

echo "==> Device installation command:"
printf '    '
printf '%q ' "${START_CMD[@]}"
printf '\n'

if [[ "${RUN_START_COMMAND:-0}" == "1" ]]; then
    echo "==> Installing debug APK"
    exec "${START_CMD[@]}"
fi

echo
echo "==> Initialization complete"
echo "    Verification: ./gradlew build lint"
echo "    Install:       RUN_START_COMMAND=1 ./init.sh"
echo "    Clean build:   ./gradlew clean build"
