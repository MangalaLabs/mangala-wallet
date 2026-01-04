package com.mangala.wallet.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.core.auth.domain.model.AuthToken
import com.mangala.wallet.auth.presentation.AuthScreen
import com.mangala.wallet.auth.presentation.AuthScreenModel
import com.mangala.wallet.ui.MangalaAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var screenModel: TestAuthScreenModel
    
    @Before
    fun setup() {
        screenModel = TestAuthScreenModel()
    }
    
    // TC-E2E-001: New User Registration Journey
    @Test
    fun testNewUserRegistrationJourney() {
        // Given
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Initial state
        composeTestRule.onNodeWithText("Sign in to Mangala Wallet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign in with Passkey").assertIsDisplayed()
        
        // Click sign in
        composeTestRule.onNodeWithText("Sign in with Passkey").performClick()
        
        // Verify loading state
        screenModel.setState(AuthState.Loading)
        composeTestRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
        
        // Simulate successful registration
        screenModel.setState(
            AuthState.Authenticated.WithPasskey(
            userId = "new-user",
            credentialId = "new-cred"
        ))
    }
    
    // TC-E2E-002: Returning User Authentication
    @Test
    fun testReturningUserAuthentication() {
        // Given
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Click sign in
        composeTestRule.onNodeWithText("Sign in with Passkey").performClick()
        
        // Simulate quick authentication
        screenModel.setState(
            AuthState.Authenticated.WithPasskey(
            userId = "existing-user",
            credentialId = "existing-cred"
        ))
    }
    
    // TC-UI-001: Authentication Screen UI
    @Test
    fun testAuthenticationScreenUI() {
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Verify all UI elements
        composeTestRule.onNodeWithText("Sign in to Mangala Wallet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign in with Passkey").assertIsDisplayed()
        composeTestRule.onNodeWithText("Use another sign-in method").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Mangala Wallet Logo").assertIsDisplayed()
    }
    
    // TC-UI-002: Biometric Prompt UI
    @Test
    fun testBiometricPromptUI() {
        // Given biometric state
        screenModel.setState(AuthState.AwaitingBiometric)
        
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Verify biometric UI
        composeTestRule.onNodeWithText("Authenticate with Biometrics").assertIsDisplayed()
        composeTestRule.onNodeWithText("Use your fingerprint or face to sign in").assertIsDisplayed()
        composeTestRule.onNodeWithText("Use PIN instead").assertIsDisplayed()
    }
    
    // TC-UI-003: PIN Entry UI
    @Test
    fun testPinEntryUI() {
        // Given PIN state
        screenModel.setState(AuthState.AwaitingPin(attemptsRemaining = 3))
        
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Verify PIN UI
        composeTestRule.onNodeWithText("Enter your PIN").assertIsDisplayed()
        composeTestRule.onNodeWithText("3 attempts remaining").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("PinDigitField").assertCountEquals(4)
        composeTestRule.onNodeWithText("Forgot PIN?").assertIsDisplayed()
    }
    
    // TC-UI-004: Network Error UI
    @Test
    fun testNetworkErrorUI() {
        // Given error state
        screenModel.setState(
            AuthState.Error.NetworkError(
            message = "No internet connection"
        ))
        
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Verify error UI
        composeTestRule.onNodeWithText("No internet connection").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }
    
    // TC-UI-005: Biometric Not Available UI
    @Test
    fun testBiometricNotAvailableUI() {
        // Given
        screenModel.setBiometricAvailable(false)
        
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Click other methods
        composeTestRule.onNodeWithText("Use another sign-in method").performClick()
        
        // Biometric option should not be visible
        composeTestRule.onNodeWithText("Choose Authentication Method").assertIsDisplayed()
        composeTestRule.onNodeWithText("PIN").assertIsDisplayed()
        composeTestRule.onNodeWithText("Biometric").assertDoesNotExist()
    }
    
    // TC-A11Y-001: Screen Reader Support
    @Test
    fun testScreenReaderSupport() {
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Verify content descriptions
        composeTestRule.onNodeWithContentDescription("Mangala Wallet Logo").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Sign in with passkey button").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Other sign in options").assertIsDisplayed()
    }
    
    // TC-A11Y-002: Keyboard Navigation
    @Test
    fun testKeyboardNavigation() {
        // Given PIN entry state
        screenModel.setState(AuthState.AwaitingPin())
        
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Test PIN input focus
        composeTestRule.onNodeWithTag("PinDigitField-0").assertIsFocused()
        composeTestRule.onNodeWithTag("PinDigitField-0").performTextInput("1")
        composeTestRule.onNodeWithTag("PinDigitField-1").assertIsFocused()
    }
    
    // Test error scenarios
    @Test
    fun testPinErrorWithRetryCount() {
        // Given
        screenModel.setState(
            AuthState.Error.PinError(
            message = "Invalid PIN",
            attemptsRemaining = 2
        ))
        
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Verify error display
        composeTestRule.onNodeWithText("Invalid PIN").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 attempts remaining").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }
    
    @Test
    fun testLockedAccountState() {
        // Given
        screenModel.setState(
            AuthState.Error.PinError(
            message = "Too many failed attempts. Please try again in 5 minutes.",
            canRetry = false,
            attemptsRemaining = 0
        ))
        
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Verify locked state
        composeTestRule.onNodeWithText("Too many failed attempts. Please try again in 5 minutes.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertDoesNotExist()
    }
}

// Test implementation of AuthScreenModel
private class TestAuthScreenModel : AuthScreenModel(
    authenticationFlowManager = TestAuthFlowManager(),
    sessionManager = TestSessionManager()
) {
    private val _state = MutableStateFlow<AuthState>(AuthState.Initial)
    override val state = _state
    
    private var biometricAvailable = true
    
    fun setState(newState: AuthState) {
        _state.value = newState
    }
    
    fun setBiometricAvailable(available: Boolean) {
        biometricAvailable = available
    }
    
    override fun isBiometricAvailable(): Boolean = biometricAvailable
}

// Stub implementations
private class TestAuthFlowManager : AuthenticationFlowManager(
    passkeyManager = StubPasskeyManager(),
    passkeyRepository = StubPasskeyRepository(),
    biometryManager = StubBiometryManager(),
    pinManager = StubPinManager(),
    authRepository = StubAuthRepository(),
    sessionManager = TestSessionManager()
)

private class StubPasskeyManager : PasskeyManager {
    override suspend fun isSupported(): Boolean = true
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential = throw NotImplementedError()
    override suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): AuthenticationResult = throw NotImplementedError()
    override suspend fun deleteCredential(credentialId: String) {}
    override suspend fun getStoredCredentials(): List<StoredCredential> = emptyList()
}

private class StubPasskeyRepository : PasskeyRepository {
    override suspend fun getRegistrationOptions(userId: String): RegistrationOptions = throw NotImplementedError()
    override suspend fun verifyRegistration(credential: PasskeyCredential, userId: String): RegistrationVerificationResult = throw NotImplementedError()
    override suspend fun getAuthenticationOptions(userId: String?): AuthenticationOptions = throw NotImplementedError()
    override suspend fun verifyAuthentication(credential: PasskeyCredential, challenge: ByteArray): AuthenticationVerificationResult = throw NotImplementedError()
    override suspend fun getStoredCredentials(userId: String): CredentialListResponse = throw NotImplementedError()
    override suspend fun deleteCredential(credentialId: String) {}
}

private class StubBiometryManager : BiometryManager {
    override suspend fun authenticate(title: String, subtitle: String): Boolean = true
    override fun isBiometricAvailable(): Boolean = true
}

private class StubPinManager : PinManager {
    override suspend fun verifyPin(pin: String): Boolean = true
    override fun getRemainingAttempts(): Int = 3
}

private class StubAuthRepository : AuthRepository {
    override suspend fun refreshToken(refreshToken: String): AuthToken = throw NotImplementedError()
    override suspend fun logout(userId: String) {}
    override suspend fun validateSession(token: String): Boolean = true
}