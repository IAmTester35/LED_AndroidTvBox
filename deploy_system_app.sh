#!/bin/bash

# Script to deploy the APK as a System App (Privileged App)
# This grants the app the highest level of control, allowing silent updates and system-level operations.

PKG_NAME="com.reecotech.androidtvbox"
APK_PATH="app/build/outputs/apk/release/app-release.apk"
DEST_DIR="/system/priv-app/$PKG_NAME"

echo "Waiting for device..."
adb wait-for-device
adb root
sleep 2

echo "Mounting system as R/W..."
# Try standard remount, then specific mount commands
adb remount || adb shell mount -o rw,remount /system || adb shell mount -o rw,remount /

# Check write access
adb shell touch /system/check_write_access
if [ $? -ne 0 ]; then
    echo "❌ ERROR: Cannot write to /system partition."
    echo "❌ ERROR: Cannot write to /system partition."
    echo "Your device might have 'dm-verity' enabled or is using EROFS (Android 14+)."
    echo "PLease run these ONE-TIME setup commands manually:"
    echo "  1. adb root"
    echo "  2. adb disable-verity"
    echo "  3. adb reboot"
    echo "  4. Wait for reboot, then: adb root && adb remount"
    echo "Then run this script again."
    exit 1
fi
adb shell rm /system/check_write_access

echo "Creating system directory..."
adb shell mkdir -p $DEST_DIR

echo "Pushing APK to system partition..."
adb push $APK_PATH $DEST_DIR/app.apk
if [ $? -ne 0 ]; then
    echo "❌ ERROR: Failed to push APK."
    exit 1
fi

echo "Creating permissions whitelist..."
# Create the XML file locally first
cat <<EOF > privapp-permissions-com.reecotech.androidtvbox.xml
<?xml version="1.0" encoding="utf-8"?>
<permissions>
    <privapp-permissions package="com.reecotech.androidtvbox">
        <permission name="android.permission.INSTALL_PACKAGES"/>
        <permission name="android.permission.DELETE_PACKAGES"/>
    </privapp-permissions>
</permissions>
EOF

echo "Pushing permissions whitelist..."
adb shell mkdir -p /system/etc/permissions
adb push privapp-permissions-com.reecotech.androidtvbox.xml /system/etc/permissions/
adb shell chmod 644 /system/etc/permissions/privapp-permissions-com.reecotech.androidtvbox.xml
rm privapp-permissions-com.reecotech.androidtvbox.xml

echo "Pushing Native Libraries (if any)..."
# Try to find the libs in the build output
LIBS_DIR="app/build/intermediates/stripped_native_libs/release/out/lib"
if [ -d "$LIBS_DIR" ]; then
    echo "Found native libs. Pushing to system location..."
    # Determine arch (simplified, assumes arm64 for typical TV box, but better to push what we have)
    # We will push all archs to the app's lib dir structure which Android parses
    # Structure: /system/priv-app/PKG/lib/arm64/...
    
    adb push $LIBS_DIR/* $DEST_DIR/lib/ || echo "Warning: Failed to push native libs or no libs found in expected path."
else
    echo "No native libs directory found at $LIBS_DIR. Skipping."
fi

echo "Setting permissions..."
adb shell chmod -R 755 $DEST_DIR
adb shell chmod 644 $DEST_DIR/app.apk

echo "Setting as Home Activity (attempt)..."
# This often fails before reboot because package isn't scanned yet.
adb shell cmd package set-home-activity $PKG_NAME/.MainActivity || echo "⚠️ Warning: Could not set Home Activity yet. This is expected. It will work after reboot."

echo "Rebooting device..."
adb reboot

echo "✅ Done! App deployed. "
echo "⚠️ IMPORTANT: After reboot, if the app is not the launcher, run:"
echo "   adb shell cmd package set-home-activity $PKG_NAME/.MainActivity"
