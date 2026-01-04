package com.mangala.wallet.passkey

import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.passkey.model.*
import com.mangala.wallet.passkey.util.PasskeyLogger
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import platform.AuthenticationServices.*
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
class PasskeyManagerImpl : PasskeyManager {
    
    private var lastAuthCredential: PasskeyCredential? = null
    
    // Keep strong references to prevent garbage collection
    private var currentRegistrationDelegate: PasskeyRegistrationDelegate? = null
    private var currentAuthenticationDelegate: PasskeyAuthenticationDelegate? = null
    private var currentPresentationProvider: PasskeyPresentationContextProvider? = null
    private var currentController: ASAuthorizationController? = null
    
    override suspend fun isSupported(): Boolean {
        return NSProcessInfo.processInfo.operatingSystemVersion.useContents {
            majorVersion >= 16 // iOS 16+ supports passkeys
        }
    }
    
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential = suspendCancellableCoroutine { continuation ->
        PasskeyLogger.d("iOS PasskeyManager - Starting registration")
        PasskeyLogger.d("userId: $userId, rpId: $rpId, userName: $userName")
        
        val provider = ASAuthorizationPlatformPublicKeyCredentialProvider(rpId)
        val request = provider.createCredentialRegistrationRequestWithChallenge(
            challenge = challenge.toNSData(),
            name = userName,
            userID = userId.encodeToByteArray().toNSData()
        )
        
        // Set display name
        request.setDisplayName(userDisplayName)
        
        // Create controller with the request
        val controller = ASAuthorizationController(listOf(request))
        
        // Create delegate to handle callbacks and store reference
        val delegate = PasskeyRegistrationDelegate(continuation) { 
            // Cleanup callback
            currentRegistrationDelegate = null
            currentPresentationProvider = null
            currentController = null
        }
        currentRegistrationDelegate = delegate
        
        // Create presentation context provider and store reference
        val presentationProvider = PasskeyPresentationContextProvider()
        currentPresentationProvider = presentationProvider
        
        // Store controller reference
        currentController = controller
        
        controller.delegate = delegate
        controller.presentationContextProvider = presentationProvider
        
        PasskeyLogger.d("iOS PasskeyManager - Controller delegate set: ${controller.delegate}")
        PasskeyLogger.d("iOS PasskeyManager - Presentation provider set: ${controller.presentationContextProvider}")
        
        // Handle cancellation
        continuation.invokeOnCancellation {
            PasskeyLogger.d("Registration cancelled")
            controller.cancel()
            currentRegistrationDelegate = null
            currentPresentationProvider = null
            currentController = null
        }
        
        // Start the registration flow
        PasskeyLogger.d("iOS PasskeyManager - Calling performRequests()")
        controller.performRequests()
        PasskeyLogger.d("iOS PasskeyManager - performRequests() called, waiting for delegate callback")
        
        // Add timeout detection
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch {
            delay(5000) // 5 second timeout
            if (currentRegistrationDelegate != null) {
                PasskeyLogger.e("iOS PasskeyManager - WARNING: No delegate callback after 5 seconds!")
                PasskeyLogger.e("This usually means:")
                PasskeyLogger.e("1. AASA file not found at https://gateway.taman2h.fun/.well-known/apple-app-site-association")
                PasskeyLogger.e("2. Bundle ID mismatch in AASA file")
                PasskeyLogger.e("3. Team ID incorrect in AASA file")
                PasskeyLogger.e("Check iOS Console.app for 'Failed to verify asset links' errors")
            }
        }
    }
    
    override suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): AuthenticationResult = suspendCancellableCoroutine { continuation ->
        PasskeyLogger.d("iOS PasskeyManager - Starting authentication")
        PasskeyLogger.d("rpId: $rpId, allowCredentials: ${allowCredentials.size}")
        
        val provider = ASAuthorizationPlatformPublicKeyCredentialProvider(rpId)
        val request = provider.createCredentialAssertionRequestWithChallenge(challenge.toNSData())
        
        // Set allowed credentials if provided
        if (allowCredentials.isNotEmpty()) {
            val descriptors = allowCredentials.map { cred ->
                ASAuthorizationPlatformPublicKeyCredentialDescriptor(
                    credentialID = cred.id.toNSData()
                )
            }
            request.setAllowedCredentials(descriptors)
        }
        
        // Create controller with the request
        val controller = ASAuthorizationController(listOf(request))
        
        // Create delegate to handle callbacks and store reference
        val delegate = PasskeyAuthenticationDelegate(
            continuation,
            onCredentialReceived = { credential ->
                lastAuthCredential = credential
            },
            onComplete = {
                // Cleanup callback
                currentAuthenticationDelegate = null
                currentPresentationProvider = null
                currentController = null
            }
        )
        currentAuthenticationDelegate = delegate
        
        // Create presentation context provider and store reference
        val presentationProvider = PasskeyPresentationContextProvider()
        currentPresentationProvider = presentationProvider
        
        // Store controller reference
        currentController = controller
        
        controller.delegate = delegate
        controller.presentationContextProvider = presentationProvider
        
        // Handle cancellation
        continuation.invokeOnCancellation {
            PasskeyLogger.d("Authentication cancelled")
            controller.cancel()
            currentAuthenticationDelegate = null
            currentPresentationProvider = null
            currentController = null
        }
        
        // Start the authentication flow
        controller.performRequests()
    }
    
    override suspend fun deleteCredential(credentialId: String) {
        // iOS doesn't provide direct API to delete specific passkeys
        // User must manage them through Settings
        PasskeyLogger.w("iOS doesn't support programmatic deletion of passkeys")
    }
    
    override fun getLastAuthenticationCredential(): PasskeyCredential? {
        return lastAuthCredential
    }
}

// Delegate class for registration
@OptIn(ExperimentalForeignApi::class)
private class PasskeyRegistrationDelegate(
    private val continuation: CancellableContinuation<PasskeyCredential>,
    private val onComplete: () -> Unit = {}
) : NSObject(), ASAuthorizationControllerDelegateProtocol {
    
    init {
        PasskeyLogger.d("PasskeyRegistrationDelegate - Created")
    }
    
    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization
    ) {
        PasskeyLogger.d("iOS PasskeyManager - Registration delegate callback: didCompleteWithAuthorization")
        PasskeyLogger.d("iOS PasskeyManager - Authorization credential type: ${didCompleteWithAuthorization.credential::class.simpleName}")
        
        when (val credential = didCompleteWithAuthorization.credential) {
            is ASAuthorizationPlatformPublicKeyCredentialRegistration -> {
                try {
                    val credentialId = credential.credentialID.base64UrlEncodedString()
                    val rawId = credential.credentialID
                    val clientDataJSON = credential.rawClientDataJSON
                    val attestationObject = credential.rawAttestationObject
                    
                    PasskeyLogger.d("Credential ID: $credentialId")
                    PasskeyLogger.d("Raw ID length: ${rawId.length}")
                    PasskeyLogger.d("ClientDataJSON length: ${clientDataJSON.length}")
                    PasskeyLogger.d("AttestationObject length: ${attestationObject?.length ?: 0}")
                    
                    val passkeyCredential = PasskeyCredential(
                        id = credentialId,
                        rawId = rawId.toByteArray(),
                        type = "public-key",
                        authenticatorAttachment = "platform",
                        response = AuthenticatorAttestationResponse(
                            clientDataJSON = clientDataJSON.toByteArray(),
                            attestationObject = attestationObject?.toByteArray() ?: ByteArray(0)
                        )
                    )
                    
                    continuation.resume(passkeyCredential)
                    onComplete()
                } catch (e: Exception) {
                    PasskeyLogger.e("Failed to process registration credential", e)
                    continuation.resumeWithException(
                        PasskeyException.UnknownError("Failed to process credential: ${e.message}", e)
                    )
                    onComplete()
                }
            }
            else -> {
                PasskeyLogger.e("Unexpected credential type: ${credential::class.simpleName}")
                continuation.resumeWithException(
                    PasskeyException.UnknownError("Unexpected credential type")
                )
                onComplete()
            }
        }
    }
    
    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError
    ) {
        PasskeyLogger.e("iOS PasskeyManager - Registration delegate callback: didCompleteWithError")
        PasskeyLogger.e("iOS PasskeyManager - Error code: ${didCompleteWithError.code}")
        PasskeyLogger.e("iOS PasskeyManager - Error domain: ${didCompleteWithError.domain}")
        PasskeyLogger.e("iOS PasskeyManager - Error description: ${didCompleteWithError.localizedDescription}")
        PasskeyLogger.e("iOS PasskeyManager - Error userInfo: ${didCompleteWithError.userInfo}")
        
        val exception = mapNSErrorToException(didCompleteWithError)
        continuation.resumeWithException(exception)
        onComplete()
    }
}

// Delegate class for authentication
@OptIn(ExperimentalForeignApi::class)
private class PasskeyAuthenticationDelegate(
    private val continuation: CancellableContinuation<AuthenticationResult>,
    private val onCredentialReceived: (PasskeyCredential) -> Unit,
    private val onComplete: () -> Unit = {}
) : NSObject(), ASAuthorizationControllerDelegateProtocol {
    
    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization
    ) {
        PasskeyLogger.d("iOS PasskeyManager - Authentication completed successfully")
        
        when (val credential = didCompleteWithAuthorization.credential) {
            is ASAuthorizationPlatformPublicKeyCredentialAssertion -> {
                try {
                    val credentialId = credential.credentialID.base64UrlEncodedString()
                    val rawId = credential.credentialID
                    val clientDataJSON = credential.rawClientDataJSON
                    val authenticatorData = credential.rawAuthenticatorData
                    val signature = credential.signature
                    val userID = credential.userID
                    
                    // Validate required fields
                    if (clientDataJSON == null || authenticatorData == null || signature == null) {
                        throw PasskeyException.InvalidState("Missing required credential data")
                    }
                    
                    PasskeyLogger.d("Credential ID: $credentialId")
                    PasskeyLogger.d("User ID length: ${userID?.length ?: 0}")
                    
                    // Store the full credential
                    val passkeyCredential = PasskeyCredential(
                        id = credentialId,
                        rawId = rawId.toByteArray(),
                        type = "public-key",
                        authenticatorAttachment = "platform",
                        response = AuthenticatorAssertionResponse(
                            clientDataJSON = clientDataJSON.toByteArray(),
                            authenticatorData = authenticatorData.toByteArray(),
                            signature = signature.toByteArray(),
                            userHandle = userID?.toByteArray()
                        )
                    )
                    
                    onCredentialReceived(passkeyCredential)
                    
                    val result = AuthenticationResult(
                        credentialId = credentialId,
                        userId = userID?.toByteArray()?.decodeToString() ?: "",
                        verified = true, // iOS handles verification
                        authenticatorData = authenticatorData.toByteArray(),
                        signature = signature.toByteArray()
                    )
                    
                    continuation.resume(result)
                    onComplete()
                } catch (e: Exception) {
                    PasskeyLogger.e("Failed to process authentication credential", e)
                    continuation.resumeWithException(
                        PasskeyException.UnknownError("Failed to process credential: ${e.message}", e)
                    )
                    onComplete()
                }
            }
            else -> {
                PasskeyLogger.e("Unexpected credential type: ${credential::class.simpleName}")
                continuation.resumeWithException(
                    PasskeyException.UnknownError("Unexpected credential type")
                )
                onComplete()
            }
        }
    }
    
    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError
    ) {
        PasskeyLogger.e("iOS PasskeyManager - Authentication failed: $didCompleteWithError")
        PasskeyLogger.e("iOS PasskeyManager - Authentication failed: ${didCompleteWithError.localizedDescription}")
        PasskeyLogger.e("iOS PasskeyManager - Error code: ${didCompleteWithError.code}")
        PasskeyLogger.e("iOS PasskeyManager - Error domain: ${didCompleteWithError.domain}")
        PasskeyLogger.e("iOS PasskeyManager - Error userInfo: ${didCompleteWithError.userInfo}")
        val exception = mapNSErrorToException(didCompleteWithError)
        continuation.resumeWithException(exception)
        onComplete()
    }
}

// Presentation context provider
@OptIn(ExperimentalForeignApi::class)
private class PasskeyPresentationContextProvider : NSObject(), ASAuthorizationControllerPresentationContextProvidingProtocol {
    override fun presentationAnchorForAuthorizationController(
        controller: ASAuthorizationController
    ): ASPresentationAnchor {
        return UIApplication.sharedApplication.keyWindow 
            ?: throw PasskeyException.InvalidState("No key window available")
    }
}

// Helper function to map NSError to PasskeyException
private fun mapNSErrorToException(error: NSError): PasskeyException {
    PasskeyLogger.d("Mapping NSError to PasskeyException - Code: ${error.code}, Domain: ${error.domain}")
    return when (error.code) {
        ASAuthorizationErrorCanceled -> {
            PasskeyException.UserCancelled("User cancelled the passkey operation")
        }
        ASAuthorizationErrorFailed -> {
            PasskeyException.UnknownError("Passkey operation failed: ${error.localizedDescription}")
        }
        ASAuthorizationErrorInvalidResponse -> {
            PasskeyException.InvalidState("Invalid response from authenticator")
        }
        ASAuthorizationErrorNotHandled -> {
            PasskeyException.NotSupported("Passkey operation not handled")
        }
        ASAuthorizationErrorUnknown -> {
            PasskeyException.UnknownError("Unknown error: ${error.localizedDescription}")
        }
        else -> {
            PasskeyException.UnknownError("Error ${error.code}: ${error.localizedDescription}")
        }
    }
}

// Extension functions for data conversion
@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData {
    return this.usePinned { pinnedArray ->
        NSData.create(bytes = pinnedArray.addressOf(0), length = this.size.toULong())
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    return ByteArray(this.length.toInt()) { index ->
        this.bytes!!.reinterpret<ByteVar>()[index]
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.base64UrlEncodedString(): String {
    // Convert to base64 and then to base64url
    val base64 = this.base64EncodedStringWithOptions(0u)
    return base64
        .replace('+', '-')
        .replace('/', '_')
        .trimEnd('=')
}

// ASAuthorization error codes
private const val ASAuthorizationErrorUnknown = 1000L
private const val ASAuthorizationErrorCanceled = 1001L
private const val ASAuthorizationErrorInvalidResponse = 1002L
private const val ASAuthorizationErrorNotHandled = 1003L
private const val ASAuthorizationErrorFailed = 1004L