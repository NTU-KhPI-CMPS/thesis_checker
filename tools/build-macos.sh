#!/bin/bash

TEAM_ID=""
KEYCHAIN_PROFILE="notarytool-password"

APP_NAME="Thesis Checker"
WORKSPACE="macos/Runner.xcworkspace"
SCHEME="Runner"
CONFIGURATION="Release"

# Build & Export Paths
ARCHIVE_PATH="build/macos/Archive/Runner.xcarchive"
EXPORT_PATH="build/macos/ExportedApp"
PLIST_PATH="build/macos/ExportOptions.plist"
DMG_STAGING="build/macos/DMGStaging"
DMG_OUTPUT="build/macos/${APP_NAME}.dmg"

set -e
cd ..

echo "🚀 Step 1: Running Flutter build..."
flutter build macos --release

echo "📦 Step 2: Running Xcode archive..."
xcodebuild -workspace "$WORKSPACE" \
           -scheme "$SCHEME" \
           -configuration "$CONFIGURATION" \
           -archivePath "$ARCHIVE_PATH" \
           -quiet \
           archive

echo "📤 Step 3: Exporting and Signing App Bundle..."
mkdir -p build/macos
cat <<EOF > "$PLIST_PATH"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>method</key>
    <string>developer-id</string>
    <key>signingStyle</key>
    <string>automatic</string>
    <key>teamID</key>
    <string>${TEAM_ID}</string>
</dict>
</plist>
EOF

xcodebuild -exportArchive \
           -archivePath "$ARCHIVE_PATH" \
           -exportOptionsPlist "$PLIST_PATH" \
           -exportPath "$EXPORT_PATH" \
           -quiet

APP_BUNDLE=$(find "$EXPORT_PATH" -name "*.app" | head -n 1)

if [ -z "$APP_BUNDLE" ]; then
    echo "❌ Error: Could not find the exported .app bundle."
    exit 1
fi

echo "💿 Step 4: Preparing and Creating DMG..."
rm -rf "$DMG_STAGING"
mkdir -p "$DMG_STAGING"
cp -R "$APP_BUNDLE" "$DMG_STAGING/"

rm -f "$DMG_OUTPUT"

create-dmg \
  --volname "$APP_NAME" \
  --background tools/dmg-background.png \
  --window-size 660 420 \
  --icon-size 160 \
  --icon "$(basename "$APP_BUNDLE")" 180 170 \
  --app-drop-link 480 170 \
  "$DMG_OUTPUT" \
  "$DMG_STAGING/"

echo "⏳ Step 5: Uploading to Apple for Notarization..."
xcrun notarytool submit "$DMG_OUTPUT" \
    --keychain-profile "$KEYCHAIN_PROFILE" \
    --wait

echo "📎 Step 6: Stapling the Notarization Ticket..."
xcrun stapler staple "$DMG_OUTPUT"

echo "🎉 Success! Ready to distribute DMG located at: $DMG_OUTPUT"
