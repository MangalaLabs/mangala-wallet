package com.mangala.wallet.passkey

import android.content.Context
import android.os.Build
import androidx.credentials.*
import androidx.credentials.exceptions.*
import com.mangala.wallet.passkey.data.config.PasskeyConfig
import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.passkey.model.*
import com.mangala.wallet.passkey.util.PasskeyLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

/**
 * Clean implementation of PasskeyManager for Android without hardcoded values
 */
class PasskeyManagerClean : PasskeyManager, KoinComponent {
    private val context: Context by inject()
    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }
    
    // Temporary storage for the last authentication credential
    private var lastAuthenticationCredential: PasskeyCredential? = null
    
    override suspend fun isSupported(): Boolean = withContext(Dispatchers.IO) {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }
    
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential = withContext(Dispatchers.Main) {
        try {
            PasskeyLogger.d("Starting registration for rpId: $rpId")
            
            val requestJson = createRegistrationRequestJson(
                userId = userId,
                challenge = challenge,
                rpId = rpId,
                rpName = rpName,
                userName = userName,
                userDisplayName = userDisplayName
            )
            
            val createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
                requestJson = requestJson
            )
            
            val result = credentialManager.createCredential(
                context = context,
                request = createPublicKeyCredentialRequest
            )
            
            when (result) {
                is CreatePublicKeyCredentialResponse -> {
                    parseRegistrationResponse(result.registrationResponseJson)
                }
                else -> throw PasskeyException.UnknownError("Unexpected response type")
            }
        } catch (e: CreateCredentialException) {
            throw mapCreateCredentialException(e)
        } catch (e: Exception) {
            PasskeyLogger.e("Registration failed", e)
            throw PasskeyException.UnknownError("Registration failed", e)
        }
    }
    
    override suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): AuthenticationResult = withContext(Dispatchers.Main) {
        try {
            PasskeyLogger.d("Starting authentication for rpId: $rpId")
            PasskeyLogger.v("Allowed credentials count: ${allowCredentials.size}")
            
            val requestJson = createAuthenticationRequestJson(
                challenge = challenge,
                rpId = rpId,
                allowCredentials = allowCredentials
            )
            
            val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
                requestJson = requestJson
            )
            
            val getCredRequest = GetCredentialRequest(
                listOf(getPublicKeyCredentialOption)
            )
            
            val result = credentialManager.getCredential(
                context = context,
                request = getCredRequest
            )
            
            when (val credential = result.credential) {
                is PublicKeyCredential -> {
                    parseAuthenticationResponse(credential.authenticationResponseJson)
                }
                else -> throw PasskeyException.UnknownError("Unexpected credential type")
            }
        } catch (e: GetCredentialException) {
            PasskeyLogger.e("GetCredentialException: ${e.message}", e)
            throw mapGetCredentialException(e)
        } catch (e: Exception) {
            PasskeyLogger.e("Authentication failed", e)
            throw PasskeyException.UnknownError("Authentication failed", e)
        }
    }
    
    override suspend fun deleteCredential(credentialId: String) {
        // Android doesn't provide direct API to delete specific passkey
        PasskeyLogger.w("Delete credential not supported on Android")
    }
    
    override fun getLastAuthenticationCredential(): PasskeyCredential? {
        return lastAuthenticationCredential
    }
    
    private fun createRegistrationRequestJson(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): String {
        val userIdBytes = userId.toByteArray()
        val challengeBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(challenge)
        
        PasskeyLogger.v("Creating registration request with challenge: $challengeBase64")
        
        return """
            {
                "challenge": "$challengeBase64",
                "rp": {
                    "id": "$rpId",
                    "name": "$rpName"
                },
                "user": {
                    "id": "${Base64.getUrlEncoder().withoutPadding().encodeToString(userIdBytes)}",
                    "name": "$userName",
                    "displayName": "$userDisplayName"
                },
                "pubKeyCredParams": [
                    {"type": "${PasskeyConfig.PUBLIC_KEY_TYPE}", "alg": -7},
                    {"type": "${PasskeyConfig.PUBLIC_KEY_TYPE}", "alg": -257}
                ],
                "timeout": ${PasskeyConfig.DEFAULT_TIMEOUT},
                "authenticatorSelection": {
                    "authenticatorAttachment": "${PasskeyConfig.AUTHENTICATOR_ATTACHMENT_PLATFORM}",
                    "residentKey": "preferred",
                    "userVerification": "preferred"
                }
            }
        """.trimIndent()
    }
    
    private fun createAuthenticationRequestJson(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): String {
        val challengeBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(challenge)
        
        val allowCredentialsJson = if (allowCredentials.isNotEmpty()) {
            val credentials = allowCredentials.joinToString(",") { cred ->
                val credIdBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(cred.id)
                """
                    {
                        "type": "${PasskeyConfig.PUBLIC_KEY_TYPE}",
                        "id": "$credIdBase64"
                    }
                """.trimIndent()
            }
            ""","allowCredentials": [$credentials]"""
        } else {
            ""
        }
        
        return """
            {
                "challenge": "$challengeBase64",
                "rpId": "$rpId",
                "timeout": ${PasskeyConfig.DEFAULT_TIMEOUT},
                "userVerification": "preferred"
                $allowCredentialsJson
            }
        """.trimIndent()
    }
    
    private fun parseRegistrationResponse(json: String): PasskeyCredential {
        PasskeyLogger.v("Parsing registration response")
        
        // Simple JSON parsing - in production use proper JSON library
        val idMatch = """"id"\s*:\s*"([^"]+)"""".toRegex().find(json)
        val rawIdMatch = """"rawId"\s*:\s*"([^"]+)"""".toRegex().find(json)
        val clientDataJSONMatch = """"clientDataJSON"\s*:\s*"([^"]+)"""".toRegex().find(json)
        val attestationObjectMatch = """"attestationObject"\s*:\s*"([^"]+)"""".toRegex().find(json)
        val authenticatorAttachmentMatch = """"authenticatorAttachment"\s*:\s*"([^"]+)"""".toRegex().find(json)
        
        val id = idMatch?.groupValues?.get(1) ?: throw PasskeyException.UnknownError("Missing id")
        val rawId = Base64.getUrlDecoder().decode(
            rawIdMatch?.groupValues?.get(1) ?: throw PasskeyException.UnknownError("Missing rawId")
        )
        val clientDataJSON = Base64.getUrlDecoder().decode(
            clientDataJSONMatch?.groupValues?.get(1) ?: throw PasskeyException.UnknownError("Missing clientDataJSON")
        )
        val attestationObject = Base64.getUrlDecoder().decode(
            attestationObjectMatch?.groupValues?.get(1) ?: throw PasskeyException.UnknownError("Missing attestationObject")
        )
        
        val authenticatorAttachment = authenticatorAttachmentMatch?.groupValues?.get(1) 
            ?: PasskeyConfig.AUTHENTICATOR_ATTACHMENT_PLATFORM
        
        return PasskeyCredential(
            id = id,
            rawId = rawId,
            type = PasskeyConfig.PUBLIC_KEY_TYPE,
            authenticatorAttachment = authenticatorAttachment,
            clientExtensionResults = emptyMap(),
            response = AuthenticatorAttestationResponse(
                clientDataJSON = clientDataJSON,
                attestationObject = attestationObject
            )
        )
    }
    
    private fun parseAuthenticationResponse(json: String): AuthenticationResult {
        PasskeyLogger.v("Parsing authentication response")
        
        // Parse the full credential response to get all necessary fields
        val idMatch = """"id"\s*:\s*"([^"]+)"""".toRegex().find(json)
        val rawIdMatch = """"rawId"\s*:\s*"([^"]+)"""".toRegex().find(json)
        val clientDataJSONMatch = """"clientDataJSON"\s*:\s*"([^"]+)"""".toRegex().find(json)
        val authenticatorDataMatch = """"authenticatorData"\s*:\s*"([^"]+)"""".toRegex().find(json)
        val signatureMatch = """"signature"\s*:\s*"([^"]+)"""".toRegex().find(json)
        val userHandleMatch = """"userHandle"\s*:\s*"([^"]+)"""".toRegex().find(json)
        val authenticatorAttachmentMatch = """"authenticatorAttachment"\s*:\s*"([^"]+)"""".toRegex().find(json)
        
        val id = idMatch?.groupValues?.get(1) 
            ?: throw PasskeyException.UnknownError("Missing id")
        val rawId = Base64.getUrlDecoder().decode(
            rawIdMatch?.groupValues?.get(1) ?: throw PasskeyException.UnknownError("Missing rawId")
        )
        val clientDataJSON = Base64.getUrlDecoder().decode(
            clientDataJSONMatch?.groupValues?.get(1) ?: throw PasskeyException.UnknownError("Missing clientDataJSON")
        )
        val authenticatorData = Base64.getUrlDecoder().decode(
            authenticatorDataMatch?.groupValues?.get(1) 
                ?: throw PasskeyException.UnknownError("Missing authenticatorData")
        )
        val signature = Base64.getUrlDecoder().decode(
            signatureMatch?.groupValues?.get(1) 
                ?: throw PasskeyException.UnknownError("Missing signature")
        )
        val userHandle = userHandleMatch?.groupValues?.get(1)?.let {
            Base64.getUrlDecoder().decode(it)
        }
        
        val authenticatorAttachment = authenticatorAttachmentMatch?.groupValues?.get(1)
        
        // Store the full credential data
        lastAuthenticationCredential = PasskeyCredential(
            id = id,
            rawId = rawId,
            type = PasskeyConfig.PUBLIC_KEY_TYPE,
            authenticatorAttachment = authenticatorAttachment,
            clientExtensionResults = emptyMap(),
            response = AuthenticatorAssertionResponse(
                clientDataJSON = clientDataJSON,
                authenticatorData = authenticatorData,
                signature = signature,
                userHandle = userHandle
            )
        )
        
        return AuthenticationResult(
            credentialId = id,
            userId = userHandle?.decodeToString() ?: "",
            verified = false,
            authenticatorData = authenticatorData,
            signature = signature
        )
    }
    
    private fun mapCreateCredentialException(e: CreateCredentialException): PasskeyException {
        return when (e) {
            is CreateCredentialCancellationException -> PasskeyException.UserCancelled()
            is CreateCredentialInterruptedException -> PasskeyException.UserCancelled()
            is CreateCredentialProviderConfigurationException -> PasskeyException.NotSupported()
            is CreateCredentialUnknownException -> PasskeyException.UnknownError(cause = e)
            is CreateCredentialCustomException -> PasskeyException.UnknownError(
                e.errorMessage?.toString() ?: "Unknown error", e
            )
            else -> PasskeyException.UnknownError("Credential creation failed", e)
        }
    }
    
    private fun mapGetCredentialException(e: GetCredentialException): PasskeyException {
        return when (e) {
            is GetCredentialCancellationException -> PasskeyException.UserCancelled()
            is GetCredentialInterruptedException -> PasskeyException.UserCancelled()
            is GetCredentialProviderConfigurationException -> PasskeyException.NotSupported()
            is GetCredentialUnknownException -> PasskeyException.UnknownError(cause = e)
            is GetCredentialCustomException -> PasskeyException.UnknownError(
                e.errorMessage?.toString() ?: "Unknown error", e
            )
            is NoCredentialException -> PasskeyException.CredentialNotFound()
            else -> PasskeyException.UnknownError("Credential retrieval failed", e)
        }
    }
}