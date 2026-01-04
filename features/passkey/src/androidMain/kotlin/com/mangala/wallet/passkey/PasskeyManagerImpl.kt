package com.mangala.wallet.passkey

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.credentials.*
import androidx.credentials.exceptions.*
import androidx.credentials.exceptions.domerrors.NotAllowedError
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import androidx.credentials.exceptions.publickeycredential.GetPublicKeyCredentialDomException
import androidx.credentials.exceptions.publickeycredential.GetPublicKeyCredentialException
import androidx.fragment.app.FragmentActivity
import com.mangala.wallet.passkey.data.config.PasskeyConfig
import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.passkey.model.*
import com.mangala.wallet.passkey.util.PasskeyLogger
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference
import java.util.*

class PasskeyManagerImpl : PasskeyManager, KoinComponent {
    private val applicationContext: Context by inject()
    private var activityRef: WeakReference<FragmentActivity>? = null
    
    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(applicationContext)
    }
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    fun setActivityContext(activity: FragmentActivity) {
        activityRef = WeakReference(activity)
    }
    
    private fun getActivityContext(): Context {
        return activityRef?.get() ?: applicationContext
    }
    
    override suspend fun isSupported(): Boolean = withContext(Dispatchers.IO) {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }
    
    @SuppressLint("PublicKeyCredential")
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential = withContext(Dispatchers.Main) {
        try {
            // Log debug info
            PasskeyLogger.d("PasskeyManagerImpl: Starting registration for rpId: $rpId")
            PasskeyLogger.d("PasskeyManagerImpl: Expected Android origin: ${PasskeyDebugInfo.getExpectedAndroidOrigin(applicationContext)}")
            PasskeyLogger.d("PasskeyManagerImpl: AssetLinks JSON for backend configuration:")
            PasskeyLogger.d(PasskeyDebugInfo.generateAssetLinksJson(applicationContext))
            
            // Use default values that match what backend typically sends
            // Backend sends timeout: 3000000ms, attestation: "direct", and multiple algorithms
            val timeout = 3000000L // 5 minutes like backend
            val attestation = "direct" // Match backend's attestation
            val algorithms = listOf(-7L, -35L, -36L, -257L, -258L, -259L, -37L, -38L, -39L, -8L) // All algorithms from backend
            
            PasskeyLogger.d("Using registration options - timeout: $timeout, attestation: $attestation, algorithms: $algorithms")
            
            val requestJson = createRegistrationRequestJson(
                userId = userId,
                challenge = challenge,
                rpId = rpId,
                rpName = rpName,
                userName = userName,
                userDisplayName = userDisplayName,
                timeout = timeout,
                attestation = attestation,
                algorithms = algorithms
            )
            
            val createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
                requestJson = requestJson
            )
            
            PasskeyLogger.d("")
            PasskeyLogger.d("========================================")
            PasskeyLogger.d("🔐 PASSKEY CREATION - CALLING CREDENTIAL MANAGER")
            PasskeyLogger.d("========================================")
            PasskeyLogger.d("📱 Request JSON:")
            try {
                val prettyJson = org.json.JSONObject(requestJson).toString(2)
                PasskeyLogger.d(prettyJson)
            } catch (e: Exception) {
                PasskeyLogger.d(requestJson)
            }
            PasskeyLogger.d("")
            PasskeyLogger.d("🔒 Calling Android CredentialManager.createCredential()")
            PasskeyLogger.d("⏳ Waiting for user biometric authentication...")
            
            val result = credentialManager.createCredential(
                context = getActivityContext(),
                request = createPublicKeyCredentialRequest
            )
            
            PasskeyLogger.d("✅ Credential created successfully!")
            
            when (result) {
                is CreatePublicKeyCredentialResponse -> {
                    PasskeyLogger.d("")
                    PasskeyLogger.d("========================================")
                    PasskeyLogger.d("🔐 PASSKEY CREATION - CREDENTIAL RECEIVED")
                    PasskeyLogger.d("========================================")
                    PasskeyLogger.d("📦 Raw credential response:")
                    try {
                        val prettyJson = org.json.JSONObject(result.registrationResponseJson).toString(2)
                        PasskeyLogger.d(prettyJson)
                    } catch (e: Exception) {
                        PasskeyLogger.d(result.registrationResponseJson)
                    }
                    
                    val credential = parseRegistrationResponse(result.registrationResponseJson)
                    
                    // Log the clientDataJSON to show the origin
                    try {
                        val response = credential.response as? AuthenticatorAttestationResponse
                        if (response != null) {
                            val clientDataJson = String(response.clientDataJSON)
                            val clientData = org.json.JSONObject(clientDataJson)
                            
                            PasskeyLogger.d("")
                            PasskeyLogger.d("🔍 Client Data Analysis:")
                            PasskeyLogger.d(clientData.toString(2))
                            
                            val origin = clientData.getString("origin")
                            val type = clientData.getString("type")
                            val challenge = clientData.getString("challenge")
                            
                            PasskeyLogger.d("")
                            PasskeyLogger.d("📌 Key Fields:")
                            PasskeyLogger.d("  • Origin: $origin")
                            PasskeyLogger.d("  • Type: $type")
                            PasskeyLogger.d("  • Challenge: ${challenge.take(20)}...")
                            PasskeyLogger.d("  • Credential ID: ${credential.id}")
                            PasskeyLogger.d("  • AttestationObject length: ${(response.attestationObject?.size ?: 0)} bytes")
                            PasskeyLogger.d("")
                            PasskeyLogger.d("✅ Passkey created successfully!")
                            PasskeyLogger.d("========================================")
                        }
                    } catch (e: Exception) {
                        PasskeyLogger.d("Failed to analyze clientDataJSON: ${e.message}")
                    }
                    
                    credential
                }
                else -> throw PasskeyException.UnknownError("Unexpected response type")
            }
        } catch (e: CreateCredentialException) {
            throw mapCreateCredentialException(e)
        } catch (e: Exception) {
            Napier.e("Registration failed", e)
            throw PasskeyException.UnknownError("Registration failed", e)
        }
    }
    
    override suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): AuthenticationResult = withContext(Dispatchers.Main) {
        try {
            PasskeyLogger.d("PasskeyManagerImpl.authenticate: Starting authentication")
            PasskeyLogger.d("PasskeyManagerImpl.authenticate: challenge length: ${challenge.size}")
            PasskeyLogger.d("PasskeyManagerImpl.authenticate: rpId: $rpId")
            PasskeyLogger.d("PasskeyManagerImpl.authenticate: allowCredentials count: ${allowCredentials.size}")
            
            val requestJson = createAuthenticationRequestJson(
                challenge = challenge,
                rpId = rpId,
                allowCredentials = allowCredentials
            )
            
            PasskeyLogger.d("PasskeyManagerImpl.authenticate: Created request JSON: $requestJson")
            
            val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
                requestJson = requestJson
            )
            
            val getCredRequest = GetCredentialRequest(
                listOf(getPublicKeyCredentialOption)
            )
            
            PasskeyLogger.d("PasskeyManagerImpl.authenticate: Calling credentialManager.getCredential()")
            val result = credentialManager.getCredential(
                context = getActivityContext(),
                request = getCredRequest
            )
            PasskeyLogger.d("PasskeyManagerImpl.authenticate: credentialManager.getCredential() returned")
            
            when (val credential = result.credential) {
                is PublicKeyCredential -> {
                    lastAuthenticationRawJson = credential.authenticationResponseJson
                    PasskeyLogger.d("PasskeyManagerImpl: Storing raw authentication JSON")
                    parseAuthenticationResponse(credential.authenticationResponseJson)
                }
                else -> throw PasskeyException.UnknownError("Unexpected credential type")
            }
        } catch (e: GetCredentialException) {
            PasskeyLogger.d("PasskeyManagerImpl.authenticate: GetCredentialException: ${e.message}")
            PasskeyLogger.d("PasskeyManagerImpl.authenticate: Exception type: ${e.javaClass.simpleName}")
            e.printStackTrace()
            throw mapGetCredentialException(e)
        } catch (e: Exception) {
            PasskeyLogger.d("PasskeyManagerImpl.authenticate: Exception: ${e.message}")
            PasskeyLogger.d("PasskeyManagerImpl.authenticate: Exception type: ${e.javaClass.simpleName}")
            e.printStackTrace()
            Napier.e("Authentication failed", e)
            throw PasskeyException.UnknownError("Authentication failed", e)
        }
    }
    
    override suspend fun deleteCredential(credentialId: String) {
        // Android doesn't provide direct API to delete specific passkey
        // This would need to be handled server-side
        Napier.w("Delete credential not supported on Android")
    }
    
    override fun getLastAuthenticationCredential(): PasskeyCredential? {
        return lastAuthenticationCredential
    }
    
    override fun getLastAuthenticationRawJson(): String? {
        return lastAuthenticationRawJson
    }
    
    private fun createRegistrationRequestJson(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String,
        timeout: Long? = null,
        attestation: String? = null,
        algorithms: List<Long>? = null
    ): String {
        val challengeBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(challenge)
        PasskeyLogger.d("PasskeyManagerImpl: Creating registration request with challenge: $challengeBase64")
        PasskeyLogger.d("PasskeyManagerImpl: Challenge bytes length: ${challenge.size}")
        
        // Build supported algorithms list - use what backend provides or default
        val pubKeyCredParams = algorithms?.map { alg ->
            RegistrationRequest.PublicKeyCredentialParameters(type = "public-key", alg = alg)
        } ?: listOf(
            RegistrationRequest.PublicKeyCredentialParameters(type = "public-key", alg = -7)
        )

        val request = RegistrationRequest(
            challenge = challengeBase64,
            rp = RegistrationRequest.RelyingParty(
                id = rpId,
                name = rpName
            ),
            user = RegistrationRequest.User(
                id = userId,  // Use userId directly from backend (already Base64)
                name = userName,
                displayName = userDisplayName
            ),
            pubKeyCredParams = pubKeyCredParams,
            timeout = timeout ?: PasskeyConfig.DEFAULT_TIMEOUT,
            authenticatorSelection = RegistrationRequest.AuthenticatorSelection(
                authenticatorAttachment = PasskeyConfig.AUTHENTICATOR_ATTACHMENT_PLATFORM,
                requireResidentKey = true,
                residentKey = "required",
                userVerification = "required"
            ),
            attestation = attestation ?: "none"
        )
        
        return json.encodeToString(request)
    }
    
    private fun createAuthenticationRequestJson(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): String {
        val challengeBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(challenge)
        PasskeyLogger.d("PasskeyManagerImpl: Authentication challenge base64url: $challengeBase64")
        
        // To allow user to choose from all available passkeys, we use empty array instead of null
        // This will show all passkeys for the given rpId
        val allowCredentialsList = if (allowCredentials.isNotEmpty()) {
            allowCredentials.map { cred ->
                val credIdBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(cred.id)
                PasskeyLogger.d("PasskeyManagerImpl: Credential ID base64url: $credIdBase64")
                AuthenticationRequest.AllowCredential(
                    type = "public-key",
                    id = credIdBase64
                )
            }
        } else {
            // Use empty array instead of null to avoid JSON serialization issues
            emptyList()
        }
        
        val request = AuthenticationRequest(
            challenge = challengeBase64,
            rpId = rpId,
            timeout = PasskeyConfig.DEFAULT_TIMEOUT,
            userVerification = "preferred",
            allowCredentials = allowCredentialsList,
        )
        
        return json.encodeToString(request)
    }
    
    private fun parseRegistrationResponse(jsonString: String): PasskeyCredential {
        return try {
            val response = json.decodeFromString<RegistrationResponseJson>(jsonString)
            
            PasskeyCredential(
                id = response.id,
                rawId = response.rawId,
                type = response.type,
                authenticatorAttachment = response.authenticatorAttachment ?: PasskeyConfig.AUTHENTICATOR_ATTACHMENT_PLATFORM,
                clientExtensionResults = response.clientExtensionResults,
                response = AuthenticatorAttestationResponse(
                    clientDataJSON = response.response.clientDataJSON,
                    attestationObject = response.response.attestationObject
                )
            )
        } catch (e: Exception) {
            PasskeyLogger.e("Failed to parse registration response: ${e.message}")
            throw PasskeyException.UnknownError("Failed to parse registration response: ${e.message}")
        }
    }
    
    private fun parseAuthenticationResponse(jsonString: String): AuthenticationResult {
        PasskeyLogger.d("PasskeyManagerImpl: Parsing authentication response: $jsonString")
        
        return try {
            val response = json.decodeFromString<AuthenticationResponseJson>(jsonString)
            
            lastAuthenticationCredential = PasskeyCredential(
                id = response.id,
                rawId = response.rawId,
                type = response.type,
                authenticatorAttachment = response.authenticatorAttachment,
                clientExtensionResults = response.clientExtensionResults,
                response = AuthenticatorAssertionResponse(
                    clientDataJSON = response.response.clientDataJSON,
                    authenticatorData = response.response.authenticatorData,
                    signature = response.response.signature,
                    userHandle = response.response.userHandle
                )
            )
            
            AuthenticationResult(
                credentialId = response.id,
                userId = response.response.userHandle?.decodeToString() ?: "", // Decode userHandle if present
                verified = false, // Will be set by server verification
                authenticatorData = response.response.authenticatorData,
                signature = response.response.signature
            )
        } catch (e: Exception) {
            PasskeyLogger.e("Failed to parse authentication response: ${e.message}")
            throw PasskeyException.UnknownError("Failed to parse authentication response: ${e.message}")
        }
    }
    
    // Temporary storage for the last authentication credential
    private var lastAuthenticationCredential: PasskeyCredential? = null
    private var lastAuthenticationRawJson: String? = null

    // https://developer.android.com/identity/sign-in/credential-manager-troubleshooting-guide
    private fun mapCreateCredentialException(e: CreateCredentialException): PasskeyException {
        return when (e) {
            is CreateCredentialCancellationException -> PasskeyException.UserCancelled()
            is CreateCredentialInterruptedException -> PasskeyException.UserCancelled()
            is CreateCredentialProviderConfigurationException -> PasskeyException.NotSupported()
            is CreateCredentialUnsupportedException -> PasskeyException.NotSupported()
            is CreateCredentialUnknownException -> PasskeyException.UnknownError(cause = e)
            is CreateCredentialCustomException -> PasskeyException.UnknownError(e.errorMessage?.toString() ?: "Unknown error", e)
            is CreatePublicKeyCredentialDomException -> {
                when (e.domError) {
                    is NotAllowedError -> PasskeyException.UserCancelled()
                    else -> PasskeyException.UnknownError("Credential creation failed", e)
                }
            }

            else -> PasskeyException.UnknownError("Credential creation failed", e)
        }
    }

    // https://developer.android.com/identity/sign-in/credential-manager-troubleshooting-guide
    private fun mapGetCredentialException(e: GetCredentialException): PasskeyException {
        return when (e) {
            is GetCredentialCancellationException -> PasskeyException.UserCancelled()
            is GetCredentialInterruptedException -> PasskeyException.UserCancelled()
            is GetCredentialProviderConfigurationException -> PasskeyException.NotSupported()
            is GetCredentialUnknownException -> PasskeyException.UnknownError(cause = e)
            is GetCredentialUnsupportedException -> PasskeyException.NotSupported()
            is GetCredentialCustomException -> PasskeyException.UnknownError(e.errorMessage?.toString() ?: "Unknown error", e)
            is NoCredentialException -> PasskeyException.CredentialNotFound()
            is GetPublicKeyCredentialException -> PasskeyException.PasskeyDecryptionFailed()
            is GetPublicKeyCredentialDomException -> {
                when (e.domError) {
                    is NotAllowedError -> PasskeyException.UserCancelled()
                    else -> PasskeyException.UnknownError("Credential retrieval failed", e)
                }
            }
            else -> PasskeyException.UnknownError("Credential retrieval failed", e)
        }
    }
}