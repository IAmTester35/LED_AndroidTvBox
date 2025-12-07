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
    echo "Your device might have 'dm-verity' enabled."
    echo "Try running manual commands:"
    echo "  adb root"
    echo "  adb disable-verity"
    echo "  adb reboot"
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

echo "Setting permissions..."
adb shell chmod 644 $DEST_DIR/app.apk

echo "Setting as Home Activity (Kiosk Mode)..."
adb shell cmd package set-home-activity $PKG_NAME/.MainActivity

echo "Rebooting device..."
adb reboot

echo "✅ Done! App deployed as System App and set as Launcher."
