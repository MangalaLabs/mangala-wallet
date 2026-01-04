package com.mangala.wallet.passkey.data.repository

import com.mangala.wallet.passkey.PasskeyManager
import com.mangala.wallet.passkey.data.config.PasskeyConfig
import com.mangala.wallet.passkey.data.mapper.PasskeyMapper.toDomain
import com.mangala.wallet.passkey.domain.model.*
import com.mangala.wallet.passkey.domain.repository.PasskeyDomainRepository
import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.passkey.model.PublicKeyCredentialDescriptor
import com.mangala.wallet.passkey.repository.PasskeyRepository
import io.github.aakira.napier.Napier

/**
 * Implementation of PasskeyDomainRepository
 */
class PasskeyDomainRepositoryImpl(
    private val passkeyManager: PasskeyManager,
    private val passkeyRepository: PasskeyRepository,
    private val config: PasskeyConfiguration
) : PasskeyDomainRepository {
    
    override suspend fun register(request: PasskeyRegistrationRequest): PasskeyRegistrationResult {
        return try {
            if (PasskeyConfig.ENABLE_DEBUG_LOGGING) {
                Napier.d("Starting passkey registration for user: ${request.userId}")
            }
            
            // Get registration options from backend
            val registrationOptions = passkeyRepository.getRegistrationOptions(
                userId = request.userId,
                username = request.userName
            )
            
            // Register with passkey manager
            val userIdToUse = registrationOptions.user.originalId 
                ?: throw PasskeyException.ServerError("Backend did not provide Base64 user ID - originalId is null")
            
            val credential = passkeyManager.register(
                userId = userIdToUse,
                challenge = registrationOptions.challenge,
                rpId = registrationOptions.rp.id,
                rpName = registrationOptions.rp.name,
                userName = registrationOptions.user.name,  // Use the user name from backend response
                userDisplayName = registrationOptions.user.displayName  // Use the display name from backend response
            )
            
            // Verify registration with backend
            val verificationResult = passkeyRepository.verifyRegistration(
                credential = credential,
                userId = request.userId
            )
            
            verificationResult.toDomain()
        } catch (e: PasskeyException) {
            Napier.e("Passkey registration failed", e)
            PasskeyRegistrationResult(
                success = false,
                errorMessage = when (e) {
                    is PasskeyException.UserCancelled -> "Registration cancelled by user"
                    is PasskeyException.NotSupported -> "Passkeys not supported on this device"
                    is PasskeyException.NetworkError -> "Network error: ${e.message}"
                    is PasskeyException.ServerError -> "Server error: ${e.message}"
                    else -> e.message ?: "Registration failed"
                }
            )
        } catch (e: Exception) {
            Napier.e("Unexpected error during passkey registration", e)
            PasskeyRegistrationResult(
                success = false,
                errorMessage = "Unexpected error: ${e.message}"
            )
        }
    }
    
    override suspend fun authenticate(request: PasskeyAuthenticationRequest): PasskeyAuthenticationResult {
        return try {
            if (PasskeyConfig.ENABLE_DEBUG_LOGGING) {
                Napier.d("Starting passkey authentication for user: ${request.userId}")
            }
            
            // Get authentication options from backend
            val authOptions = passkeyRepository.getAuthenticationOptions(
                userId = request.userId,
                username = request.username
            )
            
            // Authenticate with passkey manager
            val authResult = passkeyManager.authenticate(
                challenge = authOptions.challenge,
                rpId = authOptions.rpId,
                allowCredentials = authOptions.allowCredentials
            )
            
            // Try to get raw JSON first (Android platform specific)
            val rawJson = passkeyManager.getLastAuthenticationRawJson()
            
            val verificationResult = if (rawJson != null) {
                // Use raw JSON if available (Android)
                Napier.d("Using raw JSON for authentication verification")
                passkeyRepository.verifyAuthenticationRaw(rawJson)
            } else {
                // Fallback to structured approach
                Napier.d("Using structured approach for authentication verification")
                val credential = passkeyManager.getLastAuthenticationCredential()
                    ?: throw PasskeyException.UnknownError("Failed to retrieve authentication credential")
                passkeyRepository.verifyAuthentication(
                    credential = credential,
                    challenge = authOptions.challenge
                )
            }
            
            verificationResult.toDomain()
        } catch (e: PasskeyException) {
            Napier.e("Passkey authentication failed", e)
            PasskeyAuthenticationResult(
                success = false,
                errorMessage = when (e) {
                    is PasskeyException.UserCancelled -> "Authentication cancelled by user"
                    is PasskeyException.NotSupported -> "Passkeys not supported on this device"
                    is PasskeyException.CredentialNotFound -> "No credentials found"
                    is PasskeyException.NetworkError -> "Network error: ${e.message}"
                    is PasskeyException.ServerError -> "Server error: ${e.message}"
                    else -> e.message ?: "Authentication failed"
                }
            )
        } catch (e: Exception) {
            Napier.e("Unexpected error during passkey authentication", e)
            PasskeyAuthenticationResult(
                success = false,
                errorMessage = "Unexpected error: ${e.message}"
            )
        }
    }
    
    override suspend fun isSupported(): Boolean {
        return passkeyManager.isSupported()
    }
    
    override suspend fun deleteCredential(credentialId: String): Boolean {
        return try {
            passkeyManager.deleteCredential(credentialId)
            true
        } catch (e: Exception) {
            Napier.e("Failed to delete credential", e)
            false
        }
    }
    
    override fun getConfiguration(): PasskeyConfiguration {
        return config
    }
}