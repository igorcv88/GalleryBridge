#!/usr/bin/env bash
set -euo pipefail
for script in .github/scripts/*.sh; do bash -n "$script"; done
bash ./gradlew --no-daemon :engine:test :app:testDebugUnitTest :app:testReleaseUnitTest :app:lintDebug :app:lintRelease detekt :app:assembleDebug :app:assembleDebugAndroidTest :app:assembleRelease
