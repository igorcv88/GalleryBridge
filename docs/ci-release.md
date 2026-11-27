# CI, signing and releases

## Android CI

The single `android.yml` workflow runs once per branch push or manual dispatch. It does not duplicate runs via the pull_request event. Concurrency group `gallerybridge-ci-${{ github.ref }}` cancels superseded CI runs on the same branch without affecting other branches or release runs. Fork PRs do not automatically run this workflow or receive signing secrets; review external code before importing it to a trusted internal branch.

The normal CI consists of one runner and one Gradle invocation: wrapper validation, JDK 21, SDK/Build Tools 36, engine unit tests, app debug/release unit tests, Android lint for both variants, Detekt, debug APK and unsigned release APK. Gradle's setup action restores and saves caches on internal branches; `--build-cache --parallel` enables reusable task outputs and parallel project execution. No clean build, second release compilation, or intermediate APK artifact transfer is required. SDK packages are installed only when the required tools are absent.

The same runner signs the already-built release APK, verifies certificate SHA-256, package identity, version, APK signatures and checksum, and uploads the final APK and checksum with `retention-days: 1`. GitHub manages expiry; physical removal and storage accounting can lag. No intermediate debug APK artifacts are uploaded.

### Optional device tests

Use Actions → Android CI → Run workflow, enable `instrumentation`, and select a trusted branch to run the existing API 30 instrumented suite. It builds the instrumentation APK and starts one emulator on the same runner. The normal push CI and release do not launch emulators. API 37.0 was removed from the workflow after repeated emulator boot timeouts; this is not evidence that Android 17 is incompatible with the app. Local device tests remain available via `./gradlew :app:connectedDebugAndroidTest`. Unit tests do not substitute for physical Samsung Gallery/MediaSync validation.

## Signing secrets

Configure Settings → Secrets and variables → Actions:

| Secret | Value |
| --- | --- |
| KEYSTORE_BASE64 | Base64-encoded JKS/PKCS12 keystore |
| KEYSTORE_PASSWORD | Keystore password |
| KEY_ALIAS | Signing key alias |
| KEY_PASSWORD | Signing key password |

Same contract as WaEnhancerXCommunity. Secrets are only provided to the signing step. The unsigned release APK is aligned and signed with apksigner. The signing script verifies the certificate, application ID, version, v2/v3 signature and SHA-256 checksum; temporary key material is removed on exit and is not included in Gradle caches. There is no debug-key fallback. Older installations signed with another key cannot be updated in place.

## Release

Only a merged PR into main triggers publication. The workflow checks out the exact merged commit and verifies that it is an ancestor of origin/main. It uses the same cached host checks and single build invocation, signs the existing APK, and publishes directly to GitHub Releases. No emulator or Actions artifact upload is involved. Release concurrency remains independent of CI, with `cancel-in-progress: false`; GitHub may still replace a pending run when multiple merges arrive simultaneously.

The `release:prerelease` label on a PR before merge selects a separate pre-prN-SHA tag without changing latest. Stable releases use the literal moving `latest` tag. New assets are uploaded before the tag is moved with a lease; older generated assets are removed afterward. Updates to tags and assets are not atomic. Immutable releases or protected tags may prevent this model. Android versionCode remains controlled by Gradle. A workflow introduced or changed by a merge may not be active for that merge itself. The next eligible merge uses the updated configuration.

Neither successful JVM tests nor AOSP instrumentation establishes Samsung/Google Photos remote synchronization correctness.
