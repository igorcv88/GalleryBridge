#!/usr/bin/env bash
set -euo pipefail
umask 077
for name in KEYSTORE_BASE64 KEYSTORE_PASSWORD KEY_ALIAS KEY_PASSWORD; do
  if [[ -z "${!name:-}" ]]; then
    echo "::error::Missing required signing secret: $name" >&2
    exit 1
  fi
done
BT="$ANDROID_HOME/build-tools/36.0.0"
SOURCE=app/build/outputs/apk/release/app-release-unsigned.apk
META=app/build/outputs/apk/release/output-metadata.json
test -s "$SOURCE" && test -s "$META"
tmp="$(mktemp -d "$RUNNER_TEMP/gallerybridge-signing.XXXXXXXX")"
trap 'rm -rf -- "$tmp"' EXIT
printf '%s' "$KEYSTORE_BASE64" | base64 --decode > "$tmp/key.jks"
unset KEYSTORE_BASE64
expected="$(keytool -exportcert -keystore "$tmp/key.jks" -storepass:env KEYSTORE_PASSWORD -alias "$KEY_ALIAS" | sha256sum | awk '{print $1}')"
[[ "$expected" =~ ^[0-9a-f]{64}$ ]]
"$BT/zipalign" -P 16 -f 4 "$SOURCE" "$tmp/aligned.apk"
mkdir -p dist
apk="dist/GalleryBridge-$(git rev-parse --short=12 HEAD).apk"
"$BT/apksigner" sign --ks "$tmp/key.jks" --ks-key-alias "$KEY_ALIAS" --ks-pass env:KEYSTORE_PASSWORD --key-pass env:KEY_PASSWORD --v2-signing-enabled true --v3-signing-enabled true --out "$apk" "$tmp/aligned.apk"
"$BT/zipalign" -c -P 16 4 "$apk"
report="$("$BT/apksigner" verify --verbose --print-certs "$apk")"
printf '%s\n' "$report"
actual="$(awk '/^Signer #1 certificate SHA-256 digest:/{print $NF; exit}' <<< "$report" | tr '[:upper:]' '[:lower:]')"
[[ "$actual" == "$expected" ]]
grep -Eq '^Verified using v[23] scheme .*: true$' <<< "$report"
if grep -qi 'CN=Android Debug' <<< "$report"; then
  echo "::error::Debug certificate is not permitted" >&2; exit 1
fi
"$BT/aapt" dump badging "$apk" > "$tmp/badging.txt"
python3 - "$META" "$tmp/badging.txt" <<'PY'
import json, re, sys
from pathlib import Path
m=json.loads(Path(sys.argv[1]).read_text())
b=Path(sys.argv[2]).read_text()
items=m["elements"]
assert len(items)==1 and m["applicationId"]=="dev.gallerybridge", m
p=re.search(r"^package: name='([^']+)' versionCode='([^']+)' versionName='([^']+)'", b, re.M)
assert p and p.groups()==("dev.gallerybridge",str(items[0]["versionCode"]),items[0]["versionName"]), b[:200]
assert not re.search(r"^application-debuggable", b, re.M), "debuggable APK"
print("Package and version verified:", p.groups())
PY
(cd dist && sha256sum "$(basename "$apk")" > "$(basename "$apk").sha256")
echo "Verified signed APK: $apk"
