# iOS Passkey Implementation TODO

## Current Status
- iOS passkey functionality is stubbed out and throws `NotSupported` exceptions
- This prevents the backend errors that were occurring with empty/mock data
- Android implementation is fully functional

## Required Implementation

### 1. ASAuthorizationController Integration
The iOS implementation needs to use Apple's ASAuthorizationController API for proper WebAuthn support:

```swift
import AuthenticationServices

// For registration
let provider = ASAuthorizationPlatformPublicKeyCredentialProvider(relyingPartyIdentifier: rpId)
let request = provider.createCredentialRegistrationRequest(
    challenge: challenge,
    name: userName,
    userID: userId.data(using: .utf8)!
)
```

### 2. Required Changes

#### PasskeyManagerImpl.kt (iOS)
- Implement `register()` method using ASAuthorizationController
- Implement `authenticate()` method using ASAuthorizationController  
- Handle platform-specific credential creation and assertion responses
- Properly encode responses in WebAuthn format

#### Key Components Needed
1. **ASAuthorizationControllerDelegate** implementation
2. **ASAuthorizationControllerPresentationContextProviding** implementation
3. Proper error handling for iOS-specific cases
4. Conversion between iOS credential responses and KMP models

### 3. Testing Requirements
- Test on real iOS device (passkeys require Secure Enclave)
- Test registration flow with backend
- Test authentication flow with backend
- Verify proper WebAuthn response formatting

### 4. Dependencies
- Add AuthenticationServices framework to iOS target
- Ensure iOS 16+ target (required for passkeys)

## References
- [Apple WebAuthn Documentation](https://developer.apple.com/documentation/authenticationservices/public-private_key_authentication)
- [ASAuthorizationController](https://developer.apple.com/documentation/authenticationservices/asauthorizationcontroller)
- [WebAuthn Specification](https://w3c.github.io/webauthn/)