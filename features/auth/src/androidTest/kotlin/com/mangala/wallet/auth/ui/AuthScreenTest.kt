package com.mangala.wallet.auth.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.mangala.wallet.auth.presentation.AuthScreen
import com.mangala.wallet.auth.presentation.AuthScreenModel
import com.mangala.wallet.auth.presentation.AuthState
import com.mangala.wallet.ui.MangalaAppTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var screenModel: AuthScreenModel
    private lateinit var stateFlow: MutableStateFlow<AuthState>
    
    @Before
    fun setup() {
        screenModel = mockk(relaxed = true)
        stateFlow = MutableStateFlow(AuthState.Idle)
        every { screenModel.state } returns stateFlow
    }
    
    @Test
    fun authScreen_displaysInitialState() {
        // Given
        stateFlow.value = AuthState.Idle
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Sign in to Mangala Wallet")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Sign in with Passkey")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun authScreen_showsLoadingState() {
        // Given
        stateFlow.value = AuthState.Loading("Authenticating with passkey...")
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Authenticating with passkey...")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithTag("LoadingIndicator")
            .assertIsDisplayed()
        
        // Buttons should be disabled during loading
        composeTestRule
            .onNodeWithText("Sign in with Passkey")
            .assertIsNotEnabled()
    }
    
    @Test
    fun authScreen_showsBiometricPrompt() {
        // Given
        stateFlow.value = AuthState.AwaitingBiometric
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Authenticate with Biometrics")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Use your fingerprint or face to sign in")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Use PIN instead")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun authScreen_showsPinEntry() {
        // Given
        stateFlow.value = AuthState.AwaitingPin(attemptsRemaining = 3)
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Enter your PIN")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("3 attempts remaining")
            .assertIsDisplayed()
        
        // Check PIN input fields
        composeTestRule
            .onAllNodesWithTag("PinDigitField")
            .assertCountEquals(4)
        
        composeTestRule
            .onNodeWithText("Forgot PIN?")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun authScreen_pinEntry_handlesInput() {
        // Given
        stateFlow.value = AuthState.AwaitingPin(attemptsRemaining = 3)
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Enter PIN digits
        composeTestRule
            .onNodeWithTag("PinDigitField-0")
            .performTextInput("1")
        
        composeTestRule
            .onNodeWithTag("PinDigitField-1")
            .performTextInput("2")
        
        composeTestRule
            .onNodeWithTag("PinDigitField-2")
            .performTextInput("3")
        
        composeTestRule
            .onNodeWithTag("PinDigitField-3")
            .performTextInput("4")
        
        // Then
        verify { screenModel.onPinEntered("1234") }
    }
    
    @Test
    fun authScreen_showsError() {
        // Given
        stateFlow.value = AuthState.Error(
            message = "Authentication failed. Please try again.",
            canRetry = true
        )
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Authentication failed. Please try again.")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Try Again")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun authScreen_authenticatedState_callsCallback() {
        // Given
        var authenticatedCalled = false
        stateFlow.value = AuthState.Authenticated
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = { authenticatedCalled = true }
                )
            }
        }
        
        // Then
        composeTestRule.waitForIdle()
        assert(authenticatedCalled)
    }
    
    @Test
    fun authScreen_clickPasskeyButton_initiatesAuth() {
        // Given
        stateFlow.value = AuthState.Idle
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        composeTestRule
            .onNodeWithText("Sign in with Passkey")
            .performClick()
        
        // Then
        verify { screenModel.authenticate() }
    }
    
    @Test
    fun authScreen_clickUseAnotherMethod_showsOptions() {
        // Given
        stateFlow.value = AuthState.Idle
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        composeTestRule
            .onNodeWithText("Use another sign-in method")
            .performClick()
        
        // Then
        composeTestRule
            .onNodeWithText("Choose Authentication Method")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Biometric")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("PIN")
            .assertIsDisplayed()
    }
    
    @Test
    fun authScreen_accessibility_contentDescriptions() {
        // Given
        stateFlow.value = AuthState.Idle
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithContentDescription("Mangala Wallet Logo")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithContentDescription("Sign in with passkey button")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithContentDescription("Other sign in options")
            .assertIsDisplayed()
    }
    
    @Test
    fun authScreen_lockedState_showsMessage() {
        // Given
        stateFlow.value = AuthState.Error(
            message = "Too many failed attempts. Please try again in 5 minutes.",
            canRetry = false
        )
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                AuthScreen(
                    screenModel = screenModel,
                    onAuthenticated = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Too many failed attempts. Please try again in 5 minutes.")
            .assertIsDisplayed()
        
        // No retry button when locked
        composeTestRule
            .onNodeWithText("Try Again")
            .assertDoesNotExist()
    }
}