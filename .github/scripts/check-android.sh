#!/usr/bin/env bash
set -euo pipefail
bash -n .github/scripts/build-signed-apk.sh .github/scripts/publish-release.sh .github/scripts/instrument-apks.sh
bash ./gradlew --no-daemon :engine:test :app:testDebugUnitTest :app:testReleaseUnitTest :app:lintDebug :app:lintRelease detekt :app:assembleDebug :app:assembleDebugAndroidTest :app:assembleRelease
