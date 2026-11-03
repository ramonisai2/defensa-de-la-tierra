#!/usr/bin/env bash
set -e
SDK="${ANDROID_HOME:-/home/ramon/Android}"
BT="$SDK/build-tools/35.0.0"
cd "$(dirname "$0")"

# Compile resources
rm -rf build/res && mkdir -p build/res
"$BT/aapt2" compile res/values/strings.xml -o build/res/
"$BT/aapt2" compile res/drawable/ic_launcher.png -o build/res/

# Compile Java
rm -rf build/classes && mkdir -p build/classes
javac -d build/classes -cp "$SDK/platforms/android-35/android.jar" src/com/defensadelatierra/miniinvaders/MainActivity.java

# Dex
rm -rf build/dex && mkdir -p build/dex
"$BT/d8" --lib "$SDK/platforms/android-35/android.jar" --output build/dex $(find build/classes -name '*.class')

# Package (link resources + manifest + assets)
rm -f build/app.apk
"$BT/aapt2" link -o build/app.apk \
  -I "$SDK/platforms/android-35/android.jar" \
  --manifest AndroidManifest.xml \
  -A assets \
  --auto-add-overlay \
  build/res/*.flat

# add classes.dex
cd build && python3 - <<'PY'
import zipfile, os
apk='app.apk'
z=zipfile.ZipFile(apk); items=[(n,z.read(n)) for n in z.namelist()]; z.close()
os.remove(apk)
nz=zipfile.ZipFile(apk,'w',zipfile.ZIP_STORED)
for n,d in items:
    nz.writestr(n,d)
nz.writestr('classes.dex', open('dex/classes.dex','rb').read())
nz.close()
PY
cd ..

# Align + sign
"$BT/zipalign" -f 4 build/app.apk build/app_aligned.apk
"$BT/apksigner" sign --ks "$HOME/.android/debug.keystore" --ks-key-alias androiddebugkey --ks-pass pass:android --key-pass pass:android build/app_aligned.apk
echo "Built: build/app_aligned.apk"