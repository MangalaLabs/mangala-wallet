package com.mangala.wallet.passkey.repository

import com.mangala.wallet.passkey.data.config.PasskeyConfig
import com.mangala.wallet.passkey.di.createHttpClient
import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.passkey.model.*
import com.mangala.wallet.passkey.util.PasskeyLogger
import com.mangala.wallet.utils.getPlatform
import com.mangala.wallet.utils.PlatformType
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.koin.core.component.KoinComponent
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
private fun ByteArray.encodeBase64Url(): String = Base64.UrlSafe.encode(this).trimEnd('=')

class PasskeyRepositoryImpl(
    private val baseUrl: String = "http://localhost:8089/api/v1",
    private val httpClient: HttpClient = createHttpClient()
) : PasskeyRepository, KoinComponent {
    
    private var sessionId: String? = null
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true // Important: This ensures fields with default values are serialized
    }
    
    /**
     * Get platform-specific origin for WebAuthn requests
     */
    private fun getPlatformOrigin(): String {
        return when (getPlatform().type) {
            PlatformType.ANDROID -> {
                // Get the actual APK certificate hash dynamically
                try {
                    getAndroidOrigin()
                } catch (e: Exception) {
                    PasskeyLogger.d("Failed to get dynamic Android origin, using fallback: ${e.message}")
                    // Fallback based on build configuration
                    getAndroidOriginFallback()
                }
            }
            PlatformType.IOS -> {
                // iOS origin format for Hanko Cloudns
                "ios:bundle-id:com.mangala.wallet.MangalaSpeedRun"
            }
            PlatformType.DESKTOP -> {
                // For desktop/web platforms, use the base URL
                baseUrl.substringBefore("/api")
            }
        }
    }
    
    /**
     * Platform-specific method to get Android origin (implemented in Android source set)
     */
    private fun getAndroidOrigin(): String {
        // This will be overridden in platform-specific code
        return getAndroidOriginFallback()
    }
    
    /**
     * Get fallback Android origin based on build configuration
     */
    fun getAndroidOriginFallback(): String {
        return try {
            // Try to detect if this is a debug build
            val isDebugBuild = isDebugBuild()
            PasskeyLogger.d("Build type detected: ${if (isDebugBuild) "DEBUG" else "RELEASE"}")
            
            if (isDebugBuild) {
                "android:apk-key-hash:quiDu9QcsOuVMAxwOM61_0J_r-d79Z9PsLjGI1vI0oY"
            } else {
                "android:apk-key-hash:l08RLdm2CjSBilYw-n3Tkt8KxtdGZpPhQIExD9kDn4g"
            }
        } catch (e: Exception) {
            PasskeyLogger.d("Failed to detect build type, using release fallback: ${e.message}")
            // Default to release if detection fails
            "android:apk-key-hash:l08RLdm2CjSBilYw-n3Tkt8KxtdGZpPhQIExD9kDn4g"
        }
    }
    
    /**
     * Platform-specific method to detect debug build (implemented in platform-specific code)
     */
    private fun isDebugBuild(): Boolean {
        // This will be overridden in platform-specific code
        return false // Default to release
    }
    
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun getRegistrationOptions(userId: String, username: String?): RegistrationOptions {
        try {
            val requestBody = RegistrationRequest(
                email = userId, // userId is now the email address
                username = username ?: userId.substringBefore('@') // Use username if provided, otherwise derive from email
            )
            PasskeyLogger.d(" Sending registration request to $baseUrl/auth/register with body: ${json.encodeToString(RegistrationRequest.serializer(), requestBody)}")
            
            val response = httpClient.post("$baseUrl/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(RegistrationRequest.serializer(), requestBody))
            }
            
            if (!response.status.isSuccess()) {
                val responseBody = response.bodyAsText()
                PasskeyLogger.d("Registration error (${response.status.value}) - Response: $responseBody")

                try {
                    val errorResponse = json.decodeFromString<ApiErrorResponse>(responseBody)
                    val errorCode = errorResponse.error?.code
                    val errorMessage = errorResponse.error?.message

                    PasskeyLogger.d("Parsed error - Code: $errorCode, Message: $errorMessage")

                    when (errorCode) {
                        "BIZ_003" -> {
                            throw PasskeyException.EmailConflict(
                                errorMessage ?: "This email address is already registered. Please use a different email or sign in instead."
                            )
                        }
                        "SYS_005" -> {
                            throw PasskeyException.ServerError(
                                errorMessage ?: "External service unavailable",
                                response.status.value
                            )
                        }
                        else -> {
                            throw PasskeyException.ServerError(
                                errorMessage ?: "Failed to get registration options",
                                response.status.value
                            )
                        }
                    }
                } catch (e: Exception) {
                    if (e is PasskeyException) throw e

                    PasskeyLogger.e("Failed to parse error response: ${e.message}")
                    throw PasskeyException.ServerError(
                        "Failed to get registration options",
                        response.status.value
                    )
                }
            }
            
            val responseBody = response.bodyAsText()
            PasskeyLogger.d(" Registration response: $responseBody")
            val startResponse = json.decodeFromString<StartRegistrationResponse>(responseBody)
            
            // Store session ID for next step
            sessionId = startResponse.sessionId
            
            // Convert from backend format to our domain model
            // Handle both response formats (nested publicKey or direct publicKeyCredentialCreationOptions)
            val options = startResponse.publicKey?.publicKey 
                ?: startResponse.publicKeyCredentialCreationOptions
                ?: throw PasskeyException.ServerError("Invalid response format", 0)
            
            // Extract domain from baseUrl for RP ID if backend returns localhost
            val rpId = if (options.rp.id == "localhost" || options.rp.id.isEmpty()) {
                try {
                    val url = Url(baseUrl)
                    url.host
                } catch (e: Exception) {
                    options.rp.id
                }
            } else {
                options.rp.id
            }
            
            PasskeyLogger.d(" Using RP ID: $rpId (original: ${options.rp.id})")
            
            // Debug challenge processing
            PasskeyLogger.d(" Raw challenge from server: ${options.challenge}")
            
            // The server sends the challenge as base64url encoded, we need to decode it
            val challengeBytes = try {
                // WebAuthn standard uses base64url encoding without padding
                // Manually decode base64url
                val normalizedChallenge = options.challenge
                    .replace('-', '+')
                    .replace('_', '/')
                // Add padding if needed
                val paddedChallenge = when (normalizedChallenge.length % 4) {
                    2 -> normalizedChallenge + "=="
                    3 -> normalizedChallenge + "="
                    else -> normalizedChallenge
                }
                Base64.decode(paddedChallenge)
            } catch (e: Exception) {
                PasskeyLogger.d(" Failed to decode challenge: ${e.message}")
                // If decoding fails, use the string as-is
                options.challenge.encodeToByteArray()
            }
            
            PasskeyLogger.d(" Challenge string: ${options.challenge}")
            PasskeyLogger.d(" Challenge bytes length: ${challengeBytes.size}")

            return RegistrationOptions(
                challenge = challengeBytes,
                rp = RelyingParty(
                    id = rpId,
                    name = options.rp.name
                ),
                user = User(
                    id = options.user.id.encodeToByteArray(),
                    name = options.user.name,
                    displayName = options.user.displayName,
                    originalId = options.user.id // Store the original Base64 string
                ),
                pubKeyCredParams = options.pubKeyCredParams.map {
                    PublicKeyCredentialParameters(alg = it.alg.toLong())
                },
                authenticatorSelection = options.authenticatorSelection?.let {
                    AuthenticatorSelectionCriteria(
                        authenticatorAttachment = when(it.authenticatorAttachment) {
                            "platform" -> AuthenticatorAttachment.PLATFORM
                            "cross-platform" -> AuthenticatorAttachment.CROSS_PLATFORM
                            else -> AuthenticatorAttachment.PLATFORM
                        },
                        requireResidentKey = it.requireResidentKey ?: true,
                        userVerification = when(it.userVerification) {
                            "required" -> UserVerificationRequirement.REQUIRED
                            "preferred" -> UserVerificationRequirement.PREFERRED
                            "discouraged" -> UserVerificationRequirement.DISCOURAGED
                            else -> UserVerificationRequirement.REQUIRED
                        }
                    )
                },
                attestation = when(options.attestation) {
                    "direct" -> AttestationConveyancePreference.DIRECT
                    "indirect" -> AttestationConveyancePreference.INDIRECT
                    "none" -> AttestationConveyancePreference.NONE
                    else -> AttestationConveyancePreference.DIRECT
                },
                timeout = options.timeout ?: 60000
            )
        } catch (e: Exception) {
            when (e) {
                is PasskeyException -> throw e
                else -> throw PasskeyException.NetworkError("Network error", e)
            }
        }
    }
    
    override suspend fun verifyRegistration(
        credential: PasskeyCredential,
        userId: String
    ): RegistrationVerificationResult {
        try {
            PasskeyLogger.d(" verifyRegistration - Credential ID: ${credential.id}")
            PasskeyLogger.d(" Credential authenticatorAttachment: ${credential.authenticatorAttachment}")
            PasskeyLogger.d(" Credential clientExtensionResults: ${credential.clientExtensionResults}")
            
            // Ensure we have an attestation response
            val attestationResponse = credential.response as? AuthenticatorAttestationResponse
                ?: throw PasskeyException.UnknownError("Invalid response type for registration: ${credential.response::class.simpleName}")
            
            // Convert ByteArray to Base64Url for proper WebAuthn encoding
            val rawIdBase64Url = credential.rawId.encodeBase64Url()
            val clientDataJSONBase64Url = attestationResponse.clientDataJSON.encodeBase64Url()
            val attestationObjectBase64Url = attestationResponse.attestationObject.encodeBase64Url()
                
            PasskeyLogger.d(" Raw ID length: ${credential.rawId.size} bytes")
            PasskeyLogger.d(" ClientDataJSON length: ${attestationResponse.clientDataJSON.size} bytes")
            PasskeyLogger.d(" AttestationObject length: ${attestationResponse.attestationObject.size} bytes")
            
            // Log first few bytes for debugging
            PasskeyLogger.d(" Raw ID preview: ${credential.rawId.take(16).joinToString { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }}")
            PasskeyLogger.d(" ClientDataJSON preview: ${attestationResponse.clientDataJSON.take(50).toByteArray().decodeToString()}")
            PasskeyLogger.d(" AttestationObject CBOR preview: ${attestationResponse.attestationObject.take(16).joinToString { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }}")
                
            // Determine transports based on authenticator attachment
            val transports = when (credential.authenticatorAttachment) {
                "platform" -> listOf("internal")
                "cross-platform" -> listOf("usb", "nfc", "ble")
                else -> listOf("internal") // Default fallback
            }
            
            val platformOrigin = getPlatformOrigin()
            PasskeyLogger.d(" Platform origin: $platformOrigin")
            
            val credentialRequest = CredentialRequestDto(
                id = credential.id,
                type = credential.type,
                rawId = rawIdBase64Url,
                authenticatorAttachment = credential.authenticatorAttachment ?: "platform",
                response = AuthenticatorAttestationResponseDto(
                    clientDataJSON = clientDataJSONBase64Url,
                    attestationObject = attestationObjectBase64Url,
                    transports = transports
                ),
                transports = transports,
                clientExtensionResults = credential.clientExtensionResults ?: emptyMap(),
                sessionId = sessionId,
                origin = platformOrigin
            )
            
            val requestBody = json.encodeToString(CredentialRequestDto.serializer(), credentialRequest)
            PasskeyLogger.d(" Sending credential verification to $baseUrl/auth/register/credential")
            PasskeyLogger.d(" Session ID: $sessionId")
            PasskeyLogger.d(" Full request body: $requestBody")
            
            // Debug: Log specific fields to verify they're being included
            PasskeyLogger.d(" Credential authenticatorAttachment: ${credentialRequest.authenticatorAttachment}")
            PasskeyLogger.d(" Credential transports: ${credentialRequest.transports}")
            PasskeyLogger.d(" Credential clientExtensionResults: ${credentialRequest.clientExtensionResults}")
            
            // Log individual fields for debugging
            PasskeyLogger.d(" Credential ID: ${credentialRequest.id}")
            PasskeyLogger.d(" Credential ID length: ${credentialRequest.id.length}")
            PasskeyLogger.d(" Raw ID (base64url): ${credentialRequest.rawId}")
            PasskeyLogger.d(" Raw ID length: ${credentialRequest.rawId.length}")
            PasskeyLogger.d(" ID vs RawID match: ${credentialRequest.id == credentialRequest.rawId}")
            PasskeyLogger.d(" ClientDataJSON base64url length: ${credentialRequest.response.clientDataJSON.length}")
            PasskeyLogger.d(" AttestationObject base64url length: ${credentialRequest.response.attestationObject.length}")
            
            PasskeyLogger.d(" Headers being sent:")
            PasskeyLogger.d("  X-Session-Id: $sessionId")
            PasskeyLogger.d("  Content-Type: application/json")
            PasskeyLogger.d(" Session ID also included in body")
            
            val currentSessionId = sessionId ?: throw IllegalStateException("No session ID")
            
            val response = httpClient.post("$baseUrl/auth/register/credential") {
                contentType(ContentType.Application.Json)
                headers {
                    append("X-Session-Id", currentSessionId)
                    // Try alternative header names that might not be stripped
                    append("Session-Id", currentSessionId)
                    append("Authorization", "Session $currentSessionId")
                }
                setBody(requestBody)
            }
            
            val responseText = response.bodyAsText()
            PasskeyLogger.d(" Verification response status: ${response.status}")
            PasskeyLogger.d(" ===== FULL RESPONSE FROM auth/register/credential =====")
            PasskeyLogger.d(" Response body: $responseText")
            PasskeyLogger.d(" Response headers: ${response.headers.entries()}")
            
            // Pretty print JSON response for better readability
            try {
                val jsonElement = json.parseToJsonElement(responseText)
                val prettyJson = json.encodeToString(JsonElement.serializer(), jsonElement)
                PasskeyLogger.d(" Response JSON (formatted):\n$prettyJson")
            } catch (e: Exception) {
                PasskeyLogger.d(" Could not parse response as JSON: ${e.message}")
            }
            PasskeyLogger.d(" ===== END RESPONSE FROM auth/register/credential =====")
            
            if (!response.status.isSuccess()) {
                // Try to parse error response
                val errorMessage = try {
                    if (responseText.isNotEmpty()) {
                        val errorJson = json.parseToJsonElement(responseText).jsonObject
                        errorJson["message"]?.jsonPrimitive?.content ?: errorJson["error"]?.jsonPrimitive?.content ?: responseText
                    } else {
                        // Check if this is an origin validation error
                        if (response.status.value == 500) {
                            "The server is not configured to accept mobile app registrations. Please use the web version at https://gateway.taman2h.fun or contact support to enable mobile passkey support."
                        } else {
                            "Empty response from server"
                        }
                    }
                } catch (e: Exception) {
                    responseText.ifEmpty { 
                        if (response.status.value == 500) {
                            "The server is not configured to accept mobile app registrations. Please use the web version at https://gateway.taman2h.fun or contact support to enable mobile passkey support."
                        } else {
                            "No error details from server"
                        }
                    }
                }
                
                PasskeyLogger.d(" Registration failed - Status: ${response.status.value}, Error: $errorMessage")
                
                throw PasskeyException.ServerError(
                    "Failed to verify registration: $errorMessage",
                    response.status.value
                )
            }
            
            // The backend returns a login response with tokens after successful registration
            // Try to parse as RegistrationLoginResponseDto first, then fall back to RegistrationResponseDto
            return try {
                val loginResponse = json.decodeFromString<RegistrationLoginResponseDto>(responseText)
                PasskeyLogger.d(" Registration successful, received login tokens")
                PasskeyLogger.d(" User ID: ${loginResponse.userId}")
                PasskeyLogger.d(" Session ID: ${loginResponse.sessionId}")
                
                // Log if Keycloak tokens are available
                if (loginResponse.keycloakAccessToken != null) {
                    PasskeyLogger.d(" Keycloak tokens available in registration response")
                    PasskeyLogger.d(" Using Keycloak access token for WebSocket compatibility")
                    PasskeyLogger.d(" Keycloak access token (FULL): ${loginResponse.keycloakAccessToken}")
                    PasskeyLogger.d(" Standard access token (FULL): ${loginResponse.accessToken}")
                } else {
                    PasskeyLogger.d(" No Keycloak tokens in registration response, using standard tokens")
                    PasskeyLogger.d(" Standard access token (FULL): ${loginResponse.accessToken}")
                }
                
                // Extract credential ID from the credential if available
                val credentialId = credential.id
                
                RegistrationVerificationResult(
                    verified = loginResponse.status.equals("success", ignoreCase = true),
                    credentialId = credentialId,
                    message = loginResponse.message,
                    // Include login information so the caller can handle auto-login
                    // Use Keycloak token if available, otherwise use standard token
                    token = loginResponse.keycloakAccessToken ?: loginResponse.accessToken,
                    refreshToken = loginResponse.keycloakRefreshToken ?: loginResponse.refreshToken,
                    userId = loginResponse.userId,
                    expiresIn = loginResponse.keycloakExpiresIn ?: loginResponse.expiresIn ?: 3600 // Default to 1 hour if not provided
                )
            } catch (e: Exception) {
                PasskeyLogger.e(" Failed to parse as RegistrationLoginResponseDto: ${e.message}", e)
                // Fallback to original RegistrationResponseDto format
                try {
                    val registrationResponse = json.decodeFromString<RegistrationResponseDto>(responseText)
                    PasskeyLogger.d(" Registration successful (no auto-login)")
                    RegistrationVerificationResult(
                        verified = registrationResponse.status.equals("success", ignoreCase = true),
                        credentialId = registrationResponse.credentialId ?: credential.id,
                        message = registrationResponse.message,
                        userId = registrationResponse.userId
                    )
                } catch (fallbackError: Exception) {
                    PasskeyLogger.d(" Failed to parse registration response")
                    PasskeyLogger.d(" Response was: ${responseText.take(200)}...")
                    throw PasskeyException.ServerError("Failed to parse registration response", 0)
                }
            }
        } catch (e: Exception) {
            PasskeyLogger.d(" Caught exception in verifyRegistration: ${e.message}")
            PasskeyLogger.d(" Exception type: ${e::class.simpleName}")
            PasskeyLogger.d(" Exception details: $e")
            e.printStackTrace()
            
            when (e) {
                is PasskeyException -> throw e
                is IllegalStateException -> throw PasskeyException.InvalidState(e.message ?: "Invalid state")
                else -> throw PasskeyException.NetworkError("Network error: ${e.message}", e)
            }
        }
    }
    
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun getAuthenticationOptions(userId: String?, username: String?): AuthenticationOptions {
        try {
            PasskeyLogger.d(" Starting authentication initialization")
            PasskeyLogger.d(" Base URL: $baseUrl")
            PasskeyLogger.d(" User ID: ${userId ?: "null"}")
            PasskeyLogger.d(" Username: ${username ?: "null"}")
            
                // Send request body based on parameters
            val requestBody = when {
                userId != null -> Json.encodeToString(mapOf("userId" to userId))
                username != null -> Json.encodeToString(mapOf("username" to username))
                else -> Json.encodeToString(mapOf("allowCredentialSelection" to true))
            }
            
            PasskeyLogger.d(" Login initialization request body: $requestBody")
            
            val response = httpClient.post("$baseUrl/auth/login/initialize") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            PasskeyLogger.d(" Response status: ${response.status}")
            
            if (!response.status.isSuccess()) {
                val errorBody = try {
                    response.bodyAsText()
                } catch (e: Exception) {
                    "Could not read error body"
                }
                PasskeyLogger.d(" Error response body: $errorBody")
                throw PasskeyException.ServerError(
                    "Failed to get authentication options: ${response.status.value} - $errorBody",
                    response.status.value
                )
            }
            
            val responseBody = response.bodyAsText()
            PasskeyLogger.d(" Response body: $responseBody")
            val loginResponse = json.decodeFromString<LoginInitializeResponseDto>(responseBody)
            
            // Handle both response formats
            val options = loginResponse.publicKeyCredentialRequestOptions 
                ?: loginResponse.publicKey
                ?: throw PasskeyException.ServerError("Invalid login response format", 0)
            
            // Extract domain from baseUrl for RP ID if backend returns localhost
            val rpId = if (options.rpId == "localhost" || options.rpId.isNullOrEmpty()) {
                try {
                    val url = Url(baseUrl)
                    url.host
                } catch (e: Exception) {
                    options.rpId ?: "localhost"
                }
            } else {
                options.rpId ?: "localhost"
            }
            
            PasskeyLogger.d(" Using RP ID for auth: $rpId (original: ${options.rpId})")
            
            // Log allowed credentials to debug
            PasskeyLogger.d(" Allowed credentials from server:")
            options.allowCredentials?.forEach { cred ->
                PasskeyLogger.d("   - ID: ${cred.id}, Type: ${cred.type}")
            }
            
            // Debug challenge processing
            PasskeyLogger.d(" Raw auth challenge from server: ${options.challenge}")
            
            val challengeBytes = try {
                // WebAuthn standard uses base64url encoding without padding
                // Manually decode base64url
                val normalizedChallenge = options.challenge
                    .replace('-', '+')
                    .replace('_', '/')
                // Add padding if needed
                val paddedChallenge = when (normalizedChallenge.length % 4) {
                    2 -> normalizedChallenge + "=="
                    3 -> normalizedChallenge + "="
                    else -> normalizedChallenge
                }
                Base64.decode(paddedChallenge)
            } catch (e: Exception) {
                PasskeyLogger.d(" Failed to decode auth challenge: ${e.message}")
                // If decoding fails, use the string as-is
                options.challenge.encodeToByteArray()
            }
            
            PasskeyLogger.d(" Auth challenge bytes length: ${challengeBytes.size}")
            
            return AuthenticationOptions(
                challenge = challengeBytes,
                rpId = rpId,
                userVerification = when(options.userVerification) {
                    "required" -> UserVerificationRequirement.REQUIRED
                    "preferred" -> UserVerificationRequirement.PREFERRED
                    "discouraged" -> UserVerificationRequirement.DISCOURAGED
                    else -> UserVerificationRequirement.REQUIRED
                },
                timeout = options.timeout ?: 60000,
                allowCredentials = options.allowCredentials?.map { cred ->
                    PasskeyLogger.d(" Processing allowed credential: ${cred.id}")
                    
                    // Decode the base64url credential ID from the server
                    val credentialIdBytes = try {
                        // WebAuthn uses base64url encoding
                        val normalizedId = cred.id
                            .replace('-', '+')
                            .replace('_', '/')
                        // Add padding if needed
                        val paddedId = when (normalizedId.length % 4) {
                            2 -> normalizedId + "=="
                            3 -> normalizedId + "="
                            else -> normalizedId
                        }
                        Base64.decode(paddedId)
                    } catch (e: Exception) {
                        PasskeyLogger.d(" Failed to decode credential ID: ${e.message}")
                        // Fallback to string encoding if decode fails
                        cred.id.encodeToByteArray()
                    }
                    
                    PasskeyLogger.d(" Credential ID bytes length: ${credentialIdBytes.size}")
                    
                    PublicKeyCredentialDescriptor(
                        type = cred.type,
                        id = credentialIdBytes,
                        transports = cred.transports?.map { transport ->
                            when(transport) {
                                "usb" -> AuthenticatorTransport.USB
                                "nfc" -> AuthenticatorTransport.NFC
                                "ble" -> AuthenticatorTransport.BLE
                                "internal" -> AuthenticatorTransport.INTERNAL
                                "hybrid" -> AuthenticatorTransport.HYBRID
                                else -> AuthenticatorTransport.USB
                            }
                        } ?: emptyList()
                    )
                } ?: emptyList()
            )
        } catch (e: Exception) {
            PasskeyLogger.d(" Exception in getAuthenticationOptions: ${e.message}")
            e.printStackTrace()
            when (e) {
                is PasskeyException -> throw e
                is kotlinx.coroutines.TimeoutCancellationException -> {
                    throw PasskeyException.NetworkError("Request timed out - the server might be unavailable", e)
                }
                is HttpRequestTimeoutException -> {
                    throw PasskeyException.NetworkError("HTTP request timed out after 30 seconds", e)
                }
                else -> throw PasskeyException.NetworkError("Network error: ${e.message}", e)
            }
        }
    }
    
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun verifyAuthentication(
        credential: PasskeyCredential,
        challenge: ByteArray
    ): AuthenticationVerificationResult {
        try {
            PasskeyLogger.i("===== STARTING PASSKEY AUTHENTICATION VERIFICATION =====")
            PasskeyLogger.d(" verifyAuthentication called")
            PasskeyLogger.d(" Credential ID: ${credential.id}")
            PasskeyLogger.d(" Response type: ${credential.response::class.simpleName}")
            
            // Handle both wrapper types
            val clientDataJSON: ByteArray
            val authenticatorData: ByteArray
            val signature: ByteArray
            val userHandle: ByteArray?
            
            when (val response = credential.response) {
                is AuthenticatorAssertionResponseWrapper -> {
                    clientDataJSON = response.clientDataJSON
                    authenticatorData = response.authenticatorData
                    signature = response.signature
                    userHandle = response.userHandle
                }
                is AuthenticatorAssertionResponse -> {
                    clientDataJSON = response.clientDataJSON
                    authenticatorData = response.authenticatorData
                    signature = response.signature
                    userHandle = response.userHandle
                }
                else -> {
                    throw PasskeyException.UnknownError("Unexpected response type: ${response::class.simpleName}")
                }
            }
            
            // Convert ByteArray to Base64Url for proper WebAuthn encoding
            PasskeyLogger.d(" credential.id: ${credential.id}")
            PasskeyLogger.d(" credential.rawId bytes: ${credential.rawId.size}")
            PasskeyLogger.d(" credential.rawId first few bytes: ${credential.rawId.take(10).map { it.toInt() and 0xFF }}")
            
            val rawIdBase64Url = credential.rawId.encodeBase64Url()
            PasskeyLogger.d(" rawIdBase64Url: $rawIdBase64Url")
            
            val clientDataJSONBase64Url = clientDataJSON.encodeBase64Url()
            val authenticatorDataBase64Url = authenticatorData.encodeBase64Url()
            val signatureBase64Url = signature.encodeBase64Url()
            val userHandleBase64Url = userHandle?.encodeBase64Url()
            
            PasskeyLogger.d(" clientDataJSON length: ${clientDataJSON.size}")
            PasskeyLogger.d(" clientDataJSON content: ${clientDataJSON.decodeToString()}")
            PasskeyLogger.d(" authenticatorData length: ${authenticatorData.size}")
            PasskeyLogger.d(" signature length: ${signature.size}")
            PasskeyLogger.d(" userHandle: ${userHandle?.decodeToString()}")
            
            // Origin is not needed for authentication request
            // val platformOrigin = getPlatformOrigin()
            // PasskeyLogger.d(" Platform origin for authentication: $platformOrigin")
            
            val assertionRequest = AssertionRequestDto(
                id = credential.id,
                type = credential.type,
                rawId = rawIdBase64Url,
                authenticatorAttachment = credential.authenticatorAttachment ?: "platform",
                clientExtensionResults = credential.clientExtensionResults ?: emptyMap(),
                response = AuthenticatorAssertionResponseDto(
                    clientDataJSON = clientDataJSONBase64Url,
                    authenticatorData = authenticatorDataBase64Url,
                    signature = signatureBase64Url,
                    userHandle = userHandleBase64Url
                )
            )
            
            val requestBody = json.encodeToString(AssertionRequestDto.serializer(), assertionRequest)
            PasskeyLogger.d(" Sending authentication verification to $baseUrl/auth/login/complete")
            PasskeyLogger.d(" Request body: ${requestBody.take(200)}...")
            
            val response = httpClient.post("$baseUrl/auth/login/complete") {
                contentType(ContentType.Application.Json)
                header("User-Agent", "MangalaWallet/1.0 (Android)")
                // Add forwarded headers that backend might require
                header("X-Forwarded-For", "127.0.0.1")
                header("X-Real-IP", "127.0.0.1")
                setBody(requestBody)
            }
            
            val responseBody = response.bodyAsText()
            PasskeyLogger.d(" Response status: ${response.status}")
            PasskeyLogger.d(" ===== FULL RESPONSE FROM auth/login/complete =====")
            PasskeyLogger.d(" Response body: $responseBody")
            PasskeyLogger.d(" Response headers: ${response.headers.entries()}")
            
            // Pretty print JSON response for better readability
            try {
                val jsonElement = json.parseToJsonElement(responseBody)
                val prettyJson = json.encodeToString(JsonElement.serializer(), jsonElement)
                PasskeyLogger.d(" Response JSON (formatted):\n$prettyJson")
            } catch (e: Exception) {
                PasskeyLogger.d(" Could not parse response as JSON: ${e.message}")
            }
            PasskeyLogger.d(" ===== END RESPONSE FROM auth/login/complete =====")
            
            if (!response.status.isSuccess()) {
                throw PasskeyException.ServerError(
                    "Failed to verify authentication: $responseBody",
                    response.status.value
                )
            }
            
            val loginResponse = json.decodeFromString<LoginResponseDto>(responseBody)
            
            PasskeyLogger.d(" Login response parsed - status: ${loginResponse.status}")
            PasskeyLogger.d(" Login response userId: ${loginResponse.userId}")
            PasskeyLogger.d(" Login response email: ${loginResponse.email}")
            
            // Extract email from JWT if not provided in response
            val userIdOrEmail = loginResponse.userId ?: loginResponse.email ?: try {
                // Try to extract email from JWT token
                val tokenParts = loginResponse.accessToken.split(".")
                if (tokenParts.size >= 2) {
                    // Manually decode base64url for JWT payload
                    val payloadBase64 = tokenParts[1]
                        .replace('-', '+')
                        .replace('_', '/')
                    // Add padding if needed
                    val paddedPayload = when (payloadBase64.length % 4) {
                        2 -> payloadBase64 + "=="
                        3 -> payloadBase64 + "="
                        else -> payloadBase64
                    }
                    val payload = Base64.decode(paddedPayload)
                    val payloadJson = payload.decodeToString()
                    val jsonElement = json.parseToJsonElement(payloadJson).jsonObject
                    jsonElement["email"]?.jsonPrimitive?.content
                } else null
            } catch (e: Exception) {
                PasskeyLogger.d(" Failed to extract email from JWT: ${e.message}")
                null
            }
            
            val verified = loginResponse.status.equals("success", ignoreCase = true) || loginResponse.status.equals("SUCCESS", ignoreCase = true)
            PasskeyLogger.d(" Authentication verified: $verified")
            
            // Log if Keycloak tokens are available
            if (loginResponse.keycloakAccessToken != null) {
                PasskeyLogger.d(" Keycloak tokens available in response")
                PasskeyLogger.d(" Using Keycloak access token for WebSocket compatibility")
                PasskeyLogger.d(" Keycloak access token (FULL): ${loginResponse.keycloakAccessToken}")
                PasskeyLogger.d(" Standard access token (FULL): ${loginResponse.accessToken}")
            } else {
                PasskeyLogger.d(" No Keycloak tokens in response, using standard tokens")
                PasskeyLogger.d(" Standard access token (FULL): ${loginResponse.accessToken}")
            }
            
            return AuthenticationVerificationResult(
                verified = verified,
                userId = userIdOrEmail,
                // Use standard Hanko token for WebSocket compatibility
                // The Keycloak token is stored separately if needed for other services
                token = loginResponse.accessToken,
                refreshToken = loginResponse.refreshToken,
                message = loginResponse.message,
                // Store Keycloak tokens separately if needed
                keycloakAccessToken = loginResponse.keycloakAccessToken,
                keycloakRefreshToken = loginResponse.keycloakRefreshToken
            )
        } catch (e: Exception) {
            PasskeyLogger.d(" Exception in verifyAuthentication: ${e.message}")
            e.printStackTrace()
            when (e) {
                is PasskeyException -> throw e
                else -> throw PasskeyException.NetworkError("Network error: ${e.message}", e)
            }
        }
    }
    
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun verifyAuthenticationRaw(
        rawCredentialJson: String
    ): AuthenticationVerificationResult {
        try {

            val response = httpClient.post("$baseUrl/auth/login/complete") {
                contentType(ContentType.Application.Json)
//                header("User-Agent", "MangalaWallet/1.0 (Android)")
//                // Add forwarded headers that backend might require
//                header("X-Forwarded-For", "127.0.0.1")
//                header("X-Real-IP", "127.0.0.1")
                setBody(rawCredentialJson)
            }
            
            val responseBody = response.bodyAsText()
            PasskeyLogger.d(" Response status: ${response.status}")
            PasskeyLogger.d(" Response body: $responseBody")
            
            if (!response.status.isSuccess()) {
                throw PasskeyException.ServerError("Failed to verify authentication: $responseBody")
            }
            
            // Parse successful response using existing models
            try {
                val jsonElement = json.parseToJsonElement(responseBody).jsonObject
                
                // Extract values from response
                val userId = jsonElement["userId"]?.jsonPrimitive?.content
                val accessToken = jsonElement["access_token"]?.jsonPrimitive?.content 
                    ?: jsonElement["accessToken"]?.jsonPrimitive?.content
                    ?: throw Exception("No access token in response")
                val refreshToken = jsonElement["refresh_token"]?.jsonPrimitive?.content
                    ?: jsonElement["refreshToken"]?.jsonPrimitive?.content
                val email = jsonElement["email"]?.jsonPrimitive?.content
                
                // Try to extract email from JWT if not provided
                val finalEmail = email ?: try {
                    val tokenParts = accessToken.split(".")
                    if (tokenParts.size >= 2) {
                        val payload = Base64.decode(tokenParts[1]).decodeToString()
                        val jwtElement = json.parseToJsonElement(payload).jsonObject
                        jwtElement["email"]?.jsonPrimitive?.content
                    } else null
                } catch (e: Exception) {
                    PasskeyLogger.e("Failed to extract email from JWT: ${e.message}")
                    null
                }
                
                return AuthenticationVerificationResult(
                    verified = true,
                    userId = finalEmail ?: userId,  // Prefer email as userId for consistency
                    token = accessToken,
                    refreshToken = refreshToken
                )
            } catch (e: Exception) {
                PasskeyLogger.e("Failed to parse login complete response: ${e.message}")
                PasskeyLogger.e("Response was: $responseBody")
                throw PasskeyException.UnknownError("Failed to parse authentication response: ${e.message}")
            }
        } catch (e: PasskeyException) {
            PasskeyLogger.e("Exception in verifyAuthenticationRaw: ${e.message}")
            throw e
        } catch (e: Exception) {
            PasskeyLogger.e("Unexpected error in verifyAuthenticationRaw: ${e.message}")
            throw PasskeyException.NetworkError("Network error during authentication verification: ${e.message}")
        }
    }
    
    override suspend fun getStoredCredentials(userId: String): CredentialListResponse {
        // This endpoint might not exist in the backend, return empty for now
        return CredentialListResponse(credentials = emptyList())
    }
    
    override suspend fun deleteCredential(credentialId: String) {
        // This endpoint might not exist in the backend
        // Implement when backend supports it
    }
}

// DTOs for backend communication
@Serializable
private data class RegistrationRequest(
    val email: String,
    val username: String
)

@Serializable
private data class StartRegistrationResponse(
    val status: String? = null,
    val message: String? = null,
    val sessionId: String,
    val publicKey: PublicKeyWrapper? = null,
    val publicKeyCredentialCreationOptions: PublicKeyCredentialCreationOptionsDto? = null
)

@Serializable
private data class PublicKeyWrapper(
    val publicKey: PublicKeyCredentialCreationOptionsDto
)

@Serializable
private data class PublicKeyCredentialCreationOptionsDto(
    val rp: RelyingPartyDto,
    val user: UserDto,
    val challenge: String,
    val pubKeyCredParams: List<PubKeyCredParamDto>,
    val timeout: Long? = null,
    val authenticatorSelection: AuthenticatorSelectionDto? = null,
    val attestation: String? = null
)

@Serializable
private data class RelyingPartyDto(
    val id: String,
    val name: String
)

@Serializable
private data class UserDto(
    val id: String,
    val name: String,
    val displayName: String
)

@Serializable
private data class PubKeyCredParamDto(
    val type: String = "public-key",
    val alg: Int
)

@Serializable
private data class AuthenticatorSelectionDto(
    val authenticatorAttachment: String? = null,
    val requireResidentKey: Boolean? = null,
    val residentKey: String? = null,
    val userVerification: String? = null
)

@Serializable
private data class CredentialRequestDto(
    val id: String,
    val type: String,
    val rawId: String,
    val authenticatorAttachment: String? = "platform",
    val response: AuthenticatorAttestationResponseDto,
    val transports: List<String>? = null,
    val clientExtensionResults: Map<String, JsonElement>? = emptyMap(),
    val sessionId: String? = null,
    val origin: String? = null
)

@Serializable
private data class AuthenticatorAttestationResponseDto(
    val clientDataJSON: String,
    val attestationObject: String,
    val transports: List<String>? = listOf("internal")
)

@Serializable
private data class RegistrationResponseDto(
    val status: String,
    val message: String,
    val userId: String,
    val credentialId: String? = null // Make it optional since backend might not send it
)

// Separate DTO for registration response that includes tokens
@Serializable
private data class RegistrationLoginResponseDto(
    val status: String,
    @SerialName("userId")
    val userId: String? = null,
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("expiresIn")
    val expiresIn: Long? = null,
    @SerialName("refreshExpiresIn")
    val refreshExpiresIn: Long? = null,
    val message: String,
    @SerialName("sessionId")
    val sessionId: String? = null,
    @SerialName("tokenType")
    val tokenType: String? = null,
    @SerialName("keycloakAccessToken")
    val keycloakAccessToken: String? = null,
    @SerialName("keycloakRefreshToken")
    val keycloakRefreshToken: String? = null,
    @SerialName("keycloakExpiresIn")
    val keycloakExpiresIn: Long? = null,
    @SerialName("keycloakRefreshExpiresIn")
    val keycloakRefreshExpiresIn: Long? = null
)

@Serializable
private data class LoginRequestDto(
    @SerialName("user_id")
//    val userId: String?,
    val username: String?
)

@Serializable
private data class LoginInitializeResponseDto(
    val status: String? = null,
    val message: String? = null,
    val publicKeyCredentialRequestOptions: PublicKeyCredentialRequestOptionsDto? = null,
    val publicKey: PublicKeyCredentialRequestOptionsDto? = null // Support both formats
)

@Serializable
private data class PublicKeyCredentialRequestOptionsDto(
    val challenge: String,
    val timeout: Long? = null,
    val rpId: String? = null,
    val allowCredentials: List<PublicKeyCredentialDescriptorDto>? = null,
    val userVerification: String? = null
)

@Serializable
private data class PublicKeyCredentialDescriptorDto(
    val type: String,
    val id: String,
    val transports: List<String>? = null
)

@Serializable
private data class AssertionRequestDto(
    val id: String,
    val type: String,
    val rawId: String,
    val authenticatorAttachment: String? = "platform",
    val clientExtensionResults: Map<String, JsonElement> = emptyMap(),
    val response: AuthenticatorAssertionResponseDto
)

@Serializable
private data class AuthenticatorAssertionResponseDto(
    val clientDataJSON: String,
    val authenticatorData: String,
    val signature: String,
    val userHandle: String? = null
)

@Serializable
private data class LoginResponseDto(
    val status: String,
    @SerialName("user_id")
    val userId: String? = null,
    val email: String? = null,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("expires_in")
    val expiresIn: Long,
    @SerialName("refresh_expires_in")
    val refreshExpiresIn: Long,
    val message: String,
    @SerialName("session_id")
    val sessionId: String? = null,
    @SerialName("token_type")
    val tokenType: String? = null,
    @SerialName("keycloak_access_token")
    val keycloakAccessToken: String? = null,
    @SerialName("keycloak_refresh_token")
    val keycloakRefreshToken: String? = null,
    @SerialName("keycloak_expires_in")
    val keycloakExpiresIn: Long? = null,
    @SerialName("keycloak_refresh_expires_in")
    val keycloakRefreshExpiresIn: Long? = null
)

// Add wrapper for assertion response
interface AuthenticatorAssertionResponseWrapper {
    val clientDataJSON: ByteArray
    val authenticatorData: ByteArray
    val signature: ByteArray
    val userHandle: ByteArray?
}