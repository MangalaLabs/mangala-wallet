# Kotlin Multiplatform Integration Guide

## Tổng quan

Tài liệu này hướng dẫn tích hợp dịch vụ xác thực passkey với ứng dụng Kotlin Multiplatform. Dịch vụ sử dụng WebAuthn/FIDO2 cho xác thực không mật khẩu và hỗ trợ cả đăng ký và đăng nhập.

## Cấu hình cơ bản

### Base URL
```kotlin
const val BASE_URL = "http://localhost:8089/api/v1/auth"
```

### Headers
```kotlin
const val SESSION_HEADER = "X-Session-Id"
const val CONTENT_TYPE = "application/json"
const val AUTHORIZATION = "Authorization"
```

## API Endpoints

### 1. Đăng ký (Registration)

#### Bước 1: Khởi tạo đăng ký
**Endpoint**: `POST /auth/register`

**Request:**
```kotlin
data class RegistrationRequest(
    val email: String,
    val username: String
)
```

**Response:**
```kotlin
data class StartRegistrationResponse(
    val status: String,
    val message: String,
    val sessionId: String,
    val publicKeyCredentialCreationOptions: PublicKeyCredentialCreationOptions
)

data class PublicKeyCredentialCreationOptions(
    val rp: RelyingParty,
    val user: User,
    val challenge: String,
    val pubKeyCredParams: List<PubKeyCredParam>,
    val timeout: Long?,
    val authenticatorSelection: AuthenticatorSelection?,
    val attestation: String?
)
```

#### Bước 2: Hoàn tất đăng ký
**Endpoint**: `POST /auth/register/credential`

**Headers:**
- `X-Session-Id`: Session ID từ bước 1

**Request:**
```kotlin
data class CredentialRequest(
    val id: String,
    val type: String,
    val rawId: String,
    val response: AuthenticatorAttestationResponse
)

data class AuthenticatorAttestationResponse(
    val clientDataJSON: String,
    val attestationObject: String
)
```

**Response:**
```kotlin
data class RegistrationResponse(
    val status: String,
    val message: String,
    val userId: String,
    val credentialId: String
)
```

### 2. Đăng nhập (Login)

#### Bước 1: Khởi tạo đăng nhập
**Endpoint**: `POST /auth/login/initialize`

**Request:**
```kotlin
data class LoginRequest(
    @JsonProperty("user_id")
    val userId: String
)
```

**Response:**
```kotlin
data class LoginInitializeResponse(
    val status: String,
    val message: String,
    val publicKeyCredentialRequestOptions: PublicKeyCredentialRequestOptions
)

data class PublicKeyCredentialRequestOptions(
    val challenge: String,
    val timeout: Long?,
    val rpId: String?,
    val allowCredentials: List<PublicKeyCredentialDescriptor>?,
    val userVerification: String?
)
```

#### Bước 2: Hoàn tất đăng nhập
**Endpoint**: `POST /auth/login/complete`

**Request:**
```kotlin
data class AssertionRequest(
    val id: String,
    val type: String,
    val rawId: String,
    val response: AuthenticatorAssertionResponse
)

data class AuthenticatorAssertionResponse(
    val clientDataJSON: String,
    val authenticatorData: String,
    val signature: String,
    val userHandle: String?
)
```

**Response:**
```kotlin
data class LoginResponse(
    val status: String,
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("refresh_token")
    val refreshToken: String,
    @JsonProperty("expires_in")
    val expiresIn: Long,
    @JsonProperty("refresh_expires_in")
    val refreshExpiresIn: Long,
    val message: String,
    @JsonProperty("keycloak_access_token")
    val keycloakAccessToken: String?,
    @JsonProperty("keycloak_refresh_token")
    val keycloakRefreshToken: String?,
    @JsonProperty("keycloak_expires_in")
    val keycloakExpiresIn: Long?,
    @JsonProperty("keycloak_refresh_expires_in")
    val keycloakRefreshExpiresIn: Long?
)
```

## Implementation Example

### HTTP Client Setup
```kotlin
// Common module
expect class HttpClient {
    suspend fun post(
        url: String,
        body: String,
        headers: Map<String, String> = emptyMap()
    ): HttpResponse
}

data class HttpResponse(
    val status: Int,
    val body: String,
    val headers: Map<String, String>
)
```

### Authentication Service
```kotlin
class AuthenticationService(private val httpClient: HttpClient) {
    
    suspend fun startRegistration(
        email: String,
        username: String
    ): Result<StartRegistrationResponse> {
        return try {
            val request = RegistrationRequest(email, username)
            val response = httpClient.post(
                url = "$BASE_URL/register",
                body = Json.encodeToString(request),
                headers = mapOf("Content-Type" to CONTENT_TYPE)
            )
            
            if (response.status == 200) {
                val result = Json.decodeFromString<StartRegistrationResponse>(response.body)
                Result.success(result)
            } else {
                Result.failure(Exception("Registration failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun completeRegistration(
        sessionId: String,
        credential: CredentialRequest
    ): Result<RegistrationResponse> {
        return try {
            val response = httpClient.post(
                url = "$BASE_URL/register/credential",
                body = Json.encodeToString(credential),
                headers = mapOf(
                    "Content-Type" to CONTENT_TYPE,
                    SESSION_HEADER to sessionId
                )
            )
            
            if (response.status == 200) {
                val result = Json.decodeFromString<RegistrationResponse>(response.body)
                Result.success(result)
            } else {
                Result.failure(Exception("Registration completion failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun initializeLogin(userId: String): Result<LoginInitializeResponse> {
        return try {
            val request = LoginRequest(userId)
            val response = httpClient.post(
                url = "$BASE_URL/login/initialize",
                body = Json.encodeToString(request),
                headers = mapOf("Content-Type" to CONTENT_TYPE)
            )
            
            if (response.status == 200) {
                val result = Json.decodeFromString<LoginInitializeResponse>(response.body)
                Result.success(result)
            } else {
                Result.failure(Exception("Login initialization failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun completeLogin(assertion: AssertionRequest): Result<LoginResponse> {
        return try {
            val response = httpClient.post(
                url = "$BASE_URL/login/complete",
                body = Json.encodeToString(assertion),
                headers = mapOf("Content-Type" to CONTENT_TYPE)
            )
            
            if (response.status == 200) {
                val result = Json.decodeFromString<LoginResponse>(response.body)
                Result.success(result)
            } else {
                Result.failure(Exception("Login completion failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### WebAuthn Integration

#### Android Implementation
```kotlin
// androidMain
actual class WebAuthnClient(private val activity: Activity) {
    private val credentialManager = CredentialManager.create(activity)
    
    actual suspend fun createCredential(
        options: PublicKeyCredentialCreationOptions
    ): CredentialRequest? {
        return try {
            val createRequest = CreatePublicKeyCredentialRequest(
                requestJson = Json.encodeToString(options)
            )
            
            val result = credentialManager.createCredential(
                context = activity,
                request = createRequest
            )
            
            when (result) {
                is CreatePublicKeyCredentialResponse -> {
                    parseCredentialResponse(result.registrationResponseJson)
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    actual suspend fun getCredential(
        options: PublicKeyCredentialRequestOptions
    ): AssertionRequest? {
        return try {
            val getRequest = GetCredentialRequest(
                credentialOptions = listOf(
                    GetPublicKeyCredentialOption(
                        requestJson = Json.encodeToString(options)
                    )
                )
            )
            
            val result = credentialManager.getCredential(
                context = activity,
                request = getRequest
            )
            
            when (val credential = result.credential) {
                is PublicKeyCredential -> {
                    parseAssertionResponse(credential.authenticationResponseJson)
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun parseCredentialResponse(json: String): CredentialRequest {
        // Parse WebAuthn response JSON
        // Implementation depends on JSON structure
    }
    
    private fun parseAssertionResponse(json: String): AssertionRequest {
        // Parse WebAuthn assertion JSON
        // Implementation depends on JSON structure
    }
}
```

#### iOS Implementation
```kotlin
// iosMain
actual class WebAuthnClient {
    actual suspend fun createCredential(
        options: PublicKeyCredentialCreationOptions
    ): CredentialRequest? {
        return suspendCoroutine { continuation ->
            // Use AuthenticationServices framework
            // ASAuthorizationController with ASAuthorizationPlatformPublicKeyCredentialProvider
        }
    }
    
    actual suspend fun getCredential(
        options: PublicKeyCredentialRequestOptions
    ): AssertionRequest? {
        return suspendCoroutine { continuation ->
            // Use AuthenticationServices framework for authentication
        }
    }
}
```

### Usage Example
```kotlin
class AuthRepository(
    private val authService: AuthenticationService,
    private val webAuthnClient: WebAuthnClient
) {
    
    suspend fun register(email: String, username: String): Result<String> {
        // Step 1: Start registration
        val startResult = authService.startRegistration(email, username)
        if (startResult.isFailure) return Result.failure(startResult.exceptionOrNull()!!)
        
        val startResponse = startResult.getOrNull()!!
        
        // Step 2: Create credential using WebAuthn
        val credential = webAuthnClient.createCredential(
            startResponse.publicKeyCredentialCreationOptions
        ) ?: return Result.failure(Exception("Failed to create credential"))
        
        // Step 3: Complete registration
        val completeResult = authService.completeRegistration(
            startResponse.sessionId,
            credential
        )
        
        return if (completeResult.isSuccess) {
            Result.success(completeResult.getOrNull()!!.userId)
        } else {
            Result.failure(completeResult.exceptionOrNull()!!)
        }
    }
    
    suspend fun login(userId: String): Result<LoginResponse> {
        // Step 1: Initialize login
        val initResult = authService.initializeLogin(userId)
        if (initResult.isFailure) return Result.failure(initResult.exceptionOrNull()!!)
        
        val initResponse = initResult.getOrNull()!!
        
        // Step 2: Get assertion using WebAuthn
        val assertion = webAuthnClient.getCredential(
            initResponse.publicKeyCredentialRequestOptions
        ) ?: return Result.failure(Exception("Failed to get assertion"))
        
        // Step 3: Complete login
        return authService.completeLogin(assertion)
    }
}
```

## Dependencies

### Common
```kotlin
// build.gradle.kts (commonMain)
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```

### Android
```kotlin
// build.gradle.kts (androidMain)
dependencies {
    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
}
```

### iOS
```kotlin
// Sử dụng iOS AuthenticationServices framework
// Không cần thêm dependencies Kotlin
```

## Error Handling

```kotlin
sealed class AuthError : Exception() {
    object NetworkError : AuthError()
    object InvalidCredentials : AuthError()
    object UserNotFound : AuthError()
    object RegistrationFailed : AuthError()
    object WebAuthnNotSupported : AuthError()
    data class ServerError(val code: Int, val message: String) : AuthError()
}

fun handleAuthError(error: Throwable): AuthError {
    return when (error) {
        is SocketTimeoutException -> AuthError.NetworkError
        is UnknownHostException -> AuthError.NetworkError
        // Add more specific error mappings
        else -> AuthError.ServerError(500, error.message ?: "Unknown error")
    }
}
```

## Security Notes

1. **HTTPS Only**: Trong production, đảm bảo sử dụng HTTPS
2. **Token Storage**: Lưu trữ access token an toàn (Keychain trên iOS, EncryptedSharedPreferences trên Android)
3. **Token Refresh**: Implement logic refresh token tự động
4. **Biometric Authentication**: Kết hợp với biometric authentication khi có thể
5. **Certificate Pinning**: Implement certificate pinning cho bảo mật cao hơn

## Testing

```kotlin
class AuthenticationServiceTest {
    private val mockHttpClient = mockk<HttpClient>()
    private val authService = AuthenticationService(mockHttpClient)
    
    @Test
    fun `test successful registration flow`() = runTest {
        // Mock responses
        coEvery { 
            mockHttpClient.post(any(), any(), any()) 
        } returns HttpResponse(200, mockRegistrationResponse)
        
        val result = authService.startRegistration("test@example.com", "testuser")
        
        assertTrue(result.isSuccess)
        // Assert response properties
    }
}
```

Tài liệu này cung cấp foundation để tích hợp passkey authentication với Kotlin Multiplatform. Điều chỉnh implementation theo yêu cầu cụ thể của dự án.