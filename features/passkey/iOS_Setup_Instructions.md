# iOS Passkey Setup Instructions

## iOS Project Configuration

### 1. Add Required Capabilities

In your iOS app target, add the following entitlements to your `iosApp.entitlements` file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>com.apple.developer.web-credentials</key>
    <string>YOUR_DOMAIN.com</string>
    <key>com.apple.developer.authentication-services.autofill-credential-provider</key>
    <true/>
</dict>
</plist>
```

### 2. Associated Domains Configuration

Add your domain to the Associated Domains capability:
- In Xcode, select your app target
- Go to "Signing & Capabilities"
- Add "Associated Domains" capability
- Add: `webcredentials:YOUR_DOMAIN.com`

### 3. Apple App Site Association (AASA) File

Your server must host an AASA file at `https://YOUR_DOMAIN.com/.well-known/apple-app-site-association`:

```json
{
  "webcredentials": {
    "apps": [
      "TEAM_ID.com.mangala.wallet.speedrun"
    ]
  },
  "applinks": {
    "apps": [],
    "details": []
  }
}
```
https://branch.io/resources/aasa-validator/



### 4. Minimum iOS Version

Ensure your deployment target is iOS 16.0 or later for passkey support.

### 5. Info.plist Configuration

Add the following to your `Info.plist`:

```xml
<key>NSFaceIDUsageDescription</key>
<string>Use Face ID to authenticate with passkeys</string>
<key>NSUserTrackingUsageDescription</key>
<string>This app uses passkeys for secure authentication</string>
```

## Testing Requirements

### Real Device Only
- Passkeys only work on real iOS devices, not in the simulator
- The device must have biometric authentication enabled (Face ID or Touch ID)
- The device must be running iOS 16.0 or later

### Backend Configuration
- Your backend must support WebAuthn
- The RP ID must match your domain
- The origin must be properly configured

## Troubleshooting

### Common Issues

1. **"No key window available"**: This can happen if the passkey request is triggered before the UI is fully loaded
2. **User cancelled**: User tapped "Cancel" in the passkey dialog
3. **Invalid domain**: The RP ID doesn't match your configured domain
4. **Missing entitlements**: The app doesn't have the required capabilities configured

### Debug Logging

The implementation includes debug logging. Check the Xcode console for detailed information about passkey operations.

## Example Usage

```kotlin
// In your iOS app
val passkeyManager = PasskeyManagerImpl()

// Register a new passkey
try {
    val credential = passkeyManager.register(
        userId = "user123",
        challenge = challenge,
        rpId = "your-domain.com",
        userName = "user@example.com",
        userDisplayName = "User Name"
    )
    // Registration successful
} catch (e: PasskeyException) {
    // Handle error
}

// Authenticate with passkey
try {
    val result = passkeyManager.authenticate(
        challenge = challenge,
        rpId = "your-domain.com"
    )
    // Authentication successful
} catch (e: PasskeyException) {
    // Handle error
}
```