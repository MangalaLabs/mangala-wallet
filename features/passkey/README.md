# Passkey Authentication Module

This module provides Kotlin Multiplatform support for passkey (WebAuthn) authentication across Android, iOS, and Desktop platforms.

## Features

- Platform-specific implementations for passkey authentication
- Support for registration and authentication flows
- Comprehensive exception handling
- Integration with backend passkey services (Hanko)

## Setup

### Android

1. Ensure your app targets Android API 28+ (Android 9.0)
2. Add the following to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

3. Configure your app's digital asset links for your domain

### iOS

1. Ensure your app targets iOS 16+
2. Configure Associated Domains in your app's capabilities
3. Add your domain to the webcredentials service:

```
webcredentials:example.com
```

### Desktop

Desktop implementation uses a QR code bridge approach where:
- Registration/authentication URLs are generated
- QR codes can be scanned by mobile devices
- Browser-based fallback is available

## Usage

### Module Initialization

```kotlin
val passkeyModule = passkeyModule(baseUrl = "https://api.example.com")
```

### Basic Registration

```kotlin
val passkeyManager: PasskeyManager = get() // From Koin

// Check if passkeys are supported
if (passkeyManager.isSupported()) {
    try {
        val credential = passkeyManager.register(
            userId = "user123",
            challenge = challengeFromServer,
            rpId = "example.com",
            userName = "john.doe",
            userDisplayName = "John Doe"
        )
        // Send credential to server for verification
    } catch (e: PasskeyException.UserCancelled) {
        // User cancelled the operation
    } catch (e: PasskeyException) {
        // Handle other errors
    }
}
```

### Basic Authentication

```kotlin
try {
    val result = passkeyManager.authenticate(
        challenge = challengeFromServer,
        rpId = "example.com"
    )
    // Send result to server for verification
} catch (e: PasskeyException.CredentialNotFound) {
    // No credentials found
} catch (e: PasskeyException) {
    // Handle other errors
}
```

## Exception Handling

The module provides a comprehensive exception hierarchy:

- `PasskeyException.NotSupported` - Passkey not supported on device
- `PasskeyException.UserCancelled` - User cancelled the operation
- `PasskeyException.Timeout` - Operation timed out
- `PasskeyException.CredentialNotFound` - No matching credential found
- `PasskeyException.NetworkError` - Network-related errors
- `PasskeyException.ServerError` - Server-side errors
- `PasskeyException.InvalidState` - Invalid state for operation
- `PasskeyException.UnknownError` - Unknown errors

## Platform-Specific Notes

### Android
- Uses Android Credential Manager API
- Automatically handles Play Services integration
- Supports biometric and screen lock authentication

### iOS
- Uses ASAuthorization framework
- Integrates with iCloud Keychain
- Supports Face ID and Touch ID

### Desktop
- QR code bridge for mobile authentication
- Browser-based fallback
- No local credential storage

## Security Considerations

1. Always validate challenges on the server side
2. Implement proper timeout mechanisms
3. Use HTTPS for all communications
4. Follow WebAuthn best practices
5. Implement replay attack prevention

## Testing

The module includes unit tests for core functionality. Platform-specific integration tests should be run on actual devices or emulators.

```bash
./gradlew :shared:passkey:test
```