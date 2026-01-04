# iOS Native Bridge Implementation Guide for Passkeys

## Overview
To implement real passkey support on iOS, you need to create a native Swift/Objective-C bridge that handles ASAuthorizationController.

## Step 1: Create Swift Bridge File

Create `PasskeyBridge.swift` in your iOS app:

```swift
import Foundation
import AuthenticationServices
import ComposeApp

@objc public class PasskeyBridge: NSObject {
    private var registrationContinuation: CheckedContinuation<PasskeyCredentialBridge, Error>?
    private var authenticationContinuation: CheckedContinuation<AuthenticationResultBridge, Error>?
    
    @objc public func registerPasskey(
        userId: String,
        challenge: Data,
        rpId: String,
        userName: String,
        userDisplayName: String,
        completion: @escaping (PasskeyCredentialBridge?, Error?) -> Void
    ) {
        Task {
            do {
                let credential = try await performRegistration(
                    userId: userId,
                    challenge: challenge,
                    rpId: rpId,
                    userName: userName,
                    userDisplayName: userDisplayName
                )
                completion(credential, nil)
            } catch {
                completion(nil, error)
            }
        }
    }
    
    private func performRegistration(
        userId: String,
        challenge: Data,
        rpId: String,
        userName: String,
        userDisplayName: String
    ) async throws -> PasskeyCredentialBridge {
        let provider = ASAuthorizationPlatformPublicKeyCredentialProvider(
            relyingPartyIdentifier: rpId
        )
        
        let registrationRequest = provider.createCredentialRegistrationRequest(
            challenge: challenge,
            name: userName,
            userID: userId.data(using: .utf8)!
        )
        
        registrationRequest.displayName = userDisplayName
        
        let authController = ASAuthorizationController(
            authorizationRequests: [registrationRequest]
        )
        
        return try await withCheckedThrowingContinuation { continuation in
            self.registrationContinuation = continuation
            authController.delegate = self
            authController.presentationContextProvider = self
            authController.performRequests()
        }
    }
}

// MARK: - ASAuthorizationControllerDelegate
extension PasskeyBridge: ASAuthorizationControllerDelegate {
    public func authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization authorization: ASAuthorization
    ) {
        if let credential = authorization.credential as? ASAuthorizationPlatformPublicKeyCredentialRegistration {
            let bridge = PasskeyCredentialBridge(
                id: credential.credentialID.base64EncodedString(),
                rawId: credential.credentialID,
                clientDataJSON: credential.rawClientDataJSON,
                attestationObject: credential.rawAttestationObject ?? Data()
            )
            registrationContinuation?.resume(returning: bridge)
        }
    }
    
    public func authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError error: Error
    ) {
        registrationContinuation?.resume(throwing: error)
        authenticationContinuation?.resume(throwing: error)
    }
}

// MARK: - ASAuthorizationControllerPresentationContextProviding
extension PasskeyBridge: ASAuthorizationControllerPresentationContextProviding {
    public func presentationAnchor(
        for controller: ASAuthorizationController
    ) -> ASPresentationAnchor {
        return UIApplication.shared.windows.first!
    }
}

// Bridge models
@objc public class PasskeyCredentialBridge: NSObject {
    @objc public let id: String
    @objc public let rawId: Data
    @objc public let clientDataJSON: Data
    @objc public let attestationObject: Data
    
    init(id: String, rawId: Data, clientDataJSON: Data, attestationObject: Data) {
        self.id = id
        self.rawId = rawId
        self.clientDataJSON = clientDataJSON
        self.attestationObject = attestationObject
    }
}
```

## Step 2: Update Kotlin PasskeyManagerImpl

```kotlin
// PasskeyManagerImpl.kt
package com.mangala.wallet.passkey

import kotlinx.cinterop.*
import platform.Foundation.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
actual class PasskeyManagerImpl : PasskeyManager {
    
    private val bridge = PasskeyBridge()
    
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential = suspendCancellableCoroutine { continuation ->
        bridge.registerPasskey(
            userId = userId,
            challenge = challenge.toNSData(),
            rpId = rpId,
            userName = userName,
            userDisplayName = userDisplayName
        ) { credential, error ->
            if (error != null) {
                continuation.resumeWithException(
                    PasskeyException.UnknownError(error.localizedDescription)
                )
            } else if (credential != null) {
                val passkeyCredential = PasskeyCredential(
                    id = credential.id,
                    rawId = credential.rawId.toByteArray(),
                    type = "public-key",
                    authenticatorAttachment = "platform",
                    response = AuthenticatorAttestationResponse(
                        clientDataJSON = credential.clientDataJSON.toByteArray(),
                        attestationObject = credential.attestationObject.toByteArray()
                    )
                )
                continuation.resume(passkeyCredential)
            } else {
                continuation.resumeWithException(
                    PasskeyException.UnknownError("No credential returned")
                )
            }
        }
    }
}
```

## Step 3: Configure iOS Project

1. Add to your iOS project's bridging header:
```objc
#import <ComposeApp/ComposeApp.h>
```

2. Ensure your `Info.plist` includes:
```xml
<key>NSFaceIDUsageDescription</key>
<string>Use Face ID for secure passkey authentication</string>
```

3. Add entitlements (already done):
- Associated Domains: `webcredentials:gateway.taman2h.fun`

## Step 4: Testing

1. Build and run on a real iOS device
2. The passkey flow should now use native iOS UI
3. Check Xcode console for logs

## Important Notes

- Must test on real device (not simulator)
- Device must have Face ID/Touch ID enabled
- iOS 16.0+ required
- Server must be configured to accept iOS origin format