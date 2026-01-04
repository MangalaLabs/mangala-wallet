# Hanko Cloud Mobile Origin Configuration

## Mobile App Origin Requirements

For mobile apps (Android/iOS), Hanko cloud requires specific origin configuration that differs from web applications.

## Required Hanko Cloud Settings

### 1. Allowed Origins Configuration

In your Hanko Cloud dashboard, add these origins to the **Allowed Origins** list:

```
# For Android
android:apk-key-hash:YOUR_SHA256_CERT_FINGERPRINT

# For iOS  
ios:bundle-id:com.mangala.wallet.speedrun

# For development/testing (if using localhost backend)
http://localhost:8089
https://gateway.taman2h.fun
```

### 2. Relying Party (RP) Configuration

Set your **Relying Party ID** to match your domain:
```
gateway.taman2h.fun
```

**Important**: The RP ID must match the domain in your Associated Domains (iOS) or Digital Asset Links (Android).

### 3. Mobile-Specific Origin Format

Mobile apps use special origin formats:

#### Android Origin
```
android:apk-key-hash:<SHA256_CERT_FINGERPRINT>
```

To get your SHA256 certificate fingerprint:
```bash
# Debug keystore (development)
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Production keystore
keytool -list -v -keystore /path/to/your/keystore.jks -alias your_key_alias
```

#### iOS Origin
```
ios:bundle-id:com.mangala.wallet.speedrun
```

The bundle ID must match your iOS app's bundle identifier.

## Backend Configuration Updates

### Update PasskeyRepositoryImpl Origin

In your KMP code, update the origin sent to Hanko:

```kotlin
// In PasskeyRepositoryImpl.kt
private fun getPlatformOrigin(): String {
    return when (getPlatform().name) {
        "Android" -> "android:apk-key-hash:${getAndroidCertFingerprint()}"
        "iOS" -> "ios:bundle-id:com.mangala.wallet.speedrun"
        else -> "https://gateway.taman2h.fun" // Fallback for other platforms
    }
}
```

### Update Registration Request

```kotlin
val registrationRequest = RegistrationRequestDto(
    user = UserDto(
        id = userId,
        name = userName,
        displayName = userDisplayName
    ),
    origin = getPlatformOrigin(), // Use platform-specific origin
    attestation = "direct",
    authenticatorSelection = AuthenticatorSelectionDto(
        authenticatorAttachment = "platform",
        userVerification = "required",
        requireResidentKey = true
    )
)
```

## Testing Configuration

### Development Origins
For testing, you may also need to add:
```
# Local development
http://localhost:3000
http://localhost:8089
http://127.0.0.1:8089

# Your staging domain
https://staging.gateway.taman2h.fun
```

### Production Origins
For production, only include:
```
android:apk-key-hash:YOUR_PRODUCTION_SHA256_CERT_FINGERPRINT
ios:bundle-id:com.mangala.wallet.speedrun
https://gateway.taman2h.fun
```

## Common Issues & Solutions

### Issue: "Origin not allowed" Error
**Solution**: Verify the origin format exactly matches what's configured in Hanko Cloud.

### Issue: Android APK Key Hash Mismatch
**Solution**: 
1. Get the actual SHA256 fingerprint from your signed APK
2. Update Hanko Cloud with the correct fingerprint
3. Ensure debug vs production keystores use different fingerprints

### Issue: iOS Bundle ID Mismatch
**Solution**: 
1. Check your iOS app's actual bundle identifier in Xcode
2. Ensure it matches exactly in Hanko Cloud (case-sensitive)

## Verification Steps

1. **Check Current Configuration**:
   - Log into Hanko Cloud dashboard
   - Navigate to your project settings
   - Review "Allowed Origins" section

2. **Test Origin Headers**:
   Add logging to see what origin is being sent:
   ```kotlin
   PasskeyLogger.d("Sending origin: ${getPlatformOrigin()}")
   ```

3. **Verify Certificate Fingerprints**:
   For Android, double-check your certificate fingerprint matches exactly.

## Environment-Specific Configuration

### Development
```
# Debug keystore fingerprint
android:apk-key-hash:DEVELOPMENT_SHA256_FINGERPRINT
ios:bundle-id:com.mangala.wallet.speedrun.debug
```

### Production
```
# Production keystore fingerprint  
android:apk-key-hash:PRODUCTION_SHA256_FINGERPRINT
ios:bundle-id:com.mangala.wallet.speedrun
```

This configuration should resolve the origin validation errors you've been experiencing with mobile passkey registration.