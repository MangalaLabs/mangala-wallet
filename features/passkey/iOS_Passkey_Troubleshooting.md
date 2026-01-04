# iOS Passkey Troubleshooting Guide

## Issue: Face ID succeeds but no credential callback

cd /var/www/well-known/.well-known
nano apple-app-site-association

https://gateway.taman2h.fun/.well-known/assetlinks.json
sudo systemctl restart nginx

Based on your logs, the flow is:
1. ✅ `/auth/register` call succeeds
2. ✅ iOS passkey dialog appears
3. ✅ User authenticates with Face ID
4. ❌ No delegate callback (neither success nor error)
5. ❌ No call to `/auth/register/credential`

## Possible Causes & Solutions

### 1. **Associated Domains Not Configured** (Most Likely)

The iOS device needs to verify your domain before creating passkeys.

**Check:**
```bash
# Verify AASA file is accessible
curl https://gateway.taman2h.fun/.well-known/apple-app-site-association
```

**Required AASA file content:**
```json
{
  "webcredentials": {
    "apps": ["TEAMID.com.mangala.wallet.speedrun"]
  }
}
```

**In Xcode:**
1. Select your target → Signing & Capabilities
2. Ensure "Associated Domains" capability is added
3. Verify it contains: `webcredentials:gateway.taman2h.fun`

### 2. **Bundle ID Mismatch**

Ensure your app's bundle ID matches exactly:
- In Xcode: `com.mangala.wallet.speedrun` 
- In AASA file: `TEAMID.com.mangala.wallet.speedrun`

### 3. **Team ID Issue**

Replace `TEAMID` in AASA file with your actual Apple Developer Team ID:
- Find it in Apple Developer Portal
- Or in Xcode → Signing & Capabilities → Team

### 4. **iOS Caching Issue**

iOS caches AASA files aggressively:
1. Delete the app from device
2. Restart the device
3. Reinstall the app
4. Try again

### 5. **Debug with System Logs**

Connect device to Mac and check Console.app:
1. Open Console.app
2. Select your iPhone
3. Filter for "passkey" or "webauthn"
4. Look for errors during Face ID flow

### 6. **Verify Delegate Retention**

Add this debug code to verify delegates are alive:

```kotlin
// In performRequests() section
PasskeyLogger.d("Delegate alive check after 1s")
kotlinx.coroutines.GlobalScope.launch {
    delay(1000)
    PasskeyLogger.d("Registration delegate still alive: ${currentRegistrationDelegate != null}")
    PasskeyLogger.d("Controller still alive: ${currentController != null}")
}
```

## Quick Test

To verify if it's a domain issue, temporarily try:

1. **Use a test RP ID**: Change `gateway.taman2h.fun` to `example.com` 
   - This should fail but show a different error

2. **Check Safari saved passwords**:
   - Go to Settings → Passwords
   - Check if any passkey was created for your domain

## Next Steps

1. First verify AASA file is properly deployed
2. Check Console.app for iOS system errors
3. Ensure Team ID is correct in AASA file
4. Try the delegate retention debug code

The most common issue is the AASA file not being properly configured or accessible.