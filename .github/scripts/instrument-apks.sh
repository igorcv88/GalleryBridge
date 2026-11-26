#!/usr/bin/env bash
set -euo pipefail

adb install -r android-build/debug/app-debug.apk
adb install -r android-build/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w -r dev.gallerybridge.test/dev.gallerybridge.GalleryBridgeInstrumentation |
  tee android-instrumentation.txt

grep -Eq '^OK \([1-9][0-9]* tests?\)[[:space:]]*$' android-instrumentation.txt
grep -Eq '^INSTRUMENTATION_CODE: -1[[:space:]]*$' android-instrumentation.txt
