package com.mangala.wallet.passkey

import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.passkey.model.*
import kotlinx.coroutines.runBlocking
import platform.Foundation.NSLog

/**
 * Simple test to verify iOS passkey implementation compiles and basic functionality works
 */
class PasskeyiOSTest {
    
    private val passkeyManager = PasskeyManagerImpl()
    
    fun testPasskeySupport() {
        runBlocking {
            try {
                val isSupported = passkeyManager.isSupported()
                NSLog("Passkey support: $isSupported")
                
                if (!isSupported) {
                    NSLog("Passkeys not supported on this iOS version")
                    return@runBlocking
                }
                
                // Test registration (will show UI on real device)
                testRegistration()
                
            } catch (e: Exception) {
                NSLog("Error testing passkey support: ${e.message}")
            }
        }
    }
    
    private suspend fun testRegistration() {
        try {
            NSLog("Testing passkey registration...")
            
            val challenge = "test-challenge-12345".encodeToByteArray()
            val credential = passkeyManager.register(
                userId = "test-user-123",
                challenge = challenge,
                rpId = "gateway.taman2h.fun", // Use your actual domain
                userName = "test@mangala.wallet",
                userDisplayName = "Test User"
            )
            
            NSLog("Registration successful!")
            NSLog("Credential ID: ${credential.id}")
            NSLog("Credential type: ${credential.type}")
            NSLog("Authenticator attachment: ${credential.authenticatorAttachment}")
            
            // Test authentication with the new credential
            testAuthentication(credential.id)
            
        } catch (e: PasskeyException.UserCancelled) {
            NSLog("User cancelled registration")
        } catch (e: PasskeyException) {
            NSLog("Passkey registration failed: ${e.message}")
        } catch (e: Exception) {
            NSLog("Unexpected error during registration: ${e.message}")
        }
    }
    
    private suspend fun testAuthentication(credentialId: String) {
        try {
            NSLog("Testing passkey authentication...")
            
            val challenge = "auth-challenge-67890".encodeToByteArray()
            val allowCredentials = listOf(
                PublicKeyCredentialDescriptor(
                    type = "public-key",
                    id = credentialId.encodeToByteArray(),
                    transports = listOf(AuthenticatorTransport.INTERNAL)
                )
            )
            
            val result = passkeyManager.authenticate(
                challenge = challenge,
                rpId = "gateway.taman2h.fun", // Use your actual domain
                allowCredentials = allowCredentials
            )
            
            NSLog("Authentication successful!")
            NSLog("Credential ID: ${result.credentialId}")
            NSLog("User ID: ${result.userId}")
            NSLog("Verified: ${result.verified}")
            
            // Test getting last authentication credential
            val lastCredential = passkeyManager.getLastAuthenticationCredential()
            if (lastCredential != null) {
                NSLog("Last auth credential available: ${lastCredential.id}")
            } else {
                NSLog("No last auth credential found")
            }
            
        } catch (e: PasskeyException.UserCancelled) {
            NSLog("User cancelled authentication")
        } catch (e: PasskeyException) {
            NSLog("Passkey authentication failed: ${e.message}")
        } catch (e: Exception) {
            NSLog("Unexpected error during authentication: ${e.message}")
        }
    }
}

/**
 * Call this function from your iOS app to test passkey functionality
 * 
 * Example usage in Swift:
 * ```swift
 * import shared
 * 
 * let test = PasskeyiOSTest()
 * test.testPasskeySupport()
 * ```
 */
fun testPasskeysOnIOS() {
    val test = PasskeyiOSTest()
    test.testPasskeySupport()
}