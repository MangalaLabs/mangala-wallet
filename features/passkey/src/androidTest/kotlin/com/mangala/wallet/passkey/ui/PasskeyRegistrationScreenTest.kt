package com.mangala.wallet.passkey.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.mangala.wallet.passkey.presentation.PasskeyRegistrationScreen
import com.mangala.wallet.passkey.presentation.PasskeyScreenModel
import com.mangala.wallet.passkey.presentation.PasskeyState
import com.mangala.wallet.ui.MangalaAppTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PasskeyRegistrationScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var screenModel: PasskeyScreenModel
    private lateinit var stateFlow: MutableStateFlow<PasskeyState>
    
    @Before
    fun setup() {
        screenModel = mockk(relaxed = true)
        stateFlow = MutableStateFlow(PasskeyState.Idle)
        every { screenModel.state } returns stateFlow
    }
    
    @Test
    fun registrationScreen_displaysInitialState() {
        // Given
        stateFlow.value = PasskeyState.Idle
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                PasskeyRegistrationScreen(
                    screenModel = screenModel,
                    onRegistrationComplete = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Set up Passkey")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Secure your account with a passkey")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Create Passkey")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun registrationScreen_showsCheckingSupport() {
        // Given
        stateFlow.value = PasskeyState.CheckingSupport
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                PasskeyRegistrationScreen(
                    screenModel = screenModel,
                    onRegistrationComplete = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Checking passkey support...")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithTag("LoadingIndicator")
            .assertIsDisplayed()
    }
    
    @Test
    fun registrationScreen_showsNotSupported() {
        // Given
        stateFlow.value = PasskeyState.NotSupported(
            reason = "Your device doesn't support passkeys. Please use PIN authentication instead."
        )
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                PasskeyRegistrationScreen(
                    screenModel = screenModel,
                    onRegistrationComplete = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Passkeys Not Supported")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Your device doesn't support passkeys. Please use PIN authentication instead.")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Use PIN Instead")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun registrationScreen_showsRegistering() {
        // Given
        stateFlow.value = PasskeyState.Registering
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                PasskeyRegistrationScreen(
                    screenModel = screenModel,
                    onRegistrationComplete = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Creating your passkey...")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Follow the prompts on your device")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithTag("LoadingIndicator")
            .assertIsDisplayed()
    }
    
    @Test
    fun registrationScreen_showsSuccess() {
        // Given
        stateFlow.value = PasskeyState.RegistrationSuccess
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                PasskeyRegistrationScreen(
                    screenModel = screenModel,
                    onRegistrationComplete = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Passkey Created!")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Your account is now secured with a passkey")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Continue")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun registrationScreen_showsError() {
        // Given
        stateFlow.value = PasskeyState.Error(
            message = "Failed to create passkey. Please try again.",
            canRetry = true
        )
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                PasskeyRegistrationScreen(
                    screenModel = screenModel,
                    onRegistrationComplete = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Failed to create passkey. Please try again.")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Try Again")
            .assertIsDisplayed()
            .assertIsEnabled()
        
        composeTestRule
            .onNodeWithText("Skip for Now")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun registrationScreen_clickCreatePasskey_initiatesRegistration() {
        // Given
        stateFlow.value = PasskeyState.Idle
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                PasskeyRegistrationScreen(
                    screenModel = screenModel,
                    onRegistrationComplete = {}
                )
            }
        }
        
        composeTestRule
            .onNodeWithText("Create Passkey")
            .performClick()
        
        // Then
        verify { screenModel.registerPasskey() }
    }
    
    @Test
    fun registrationScreen_successContinue_callsCallback() {
        // Given
        var completeCalled = false
        stateFlow.value = PasskeyState.RegistrationSuccess
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                PasskeyRegistrationScreen(
                    screenModel = screenModel,
                    onRegistrationComplete = { completeCalled = true }
                )
            }
        }
        
        composeTestRule
            .onNodeWithText("Continue")
            .performClick()
        
        // Then
        assert(completeCalled)
    }
    
    @Test
    fun registrationScreen_showsEducationalContent() {
        // Given
        stateFlow.value = PasskeyState.Idle
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                PasskeyRegistrationScreen(
                    screenModel = screenModel,
                    onRegistrationComplete = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("What is a passkey?")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithTag("PasskeyBenefits")
            .assertIsDisplayed()
        
        // Check benefit items
        composeTestRule
            .onNodeWithText("No passwords to remember")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Secure biometric authentication")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Works across all your devices")
            .assertIsDisplayed()
    }
    
    @Test
    fun registrationScreen_platformSpecificUI_android() {
        // Given
        stateFlow.value = PasskeyState.Registering
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                PasskeyRegistrationScreen(
                    screenModel = screenModel,
                    onRegistrationComplete = {}
                )
            }
        }
        
        // Then - Android specific UI elements
        composeTestRule
            .onNodeWithText("Google Password Manager will save your passkey")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithContentDescription("Android passkey illustration")
            .assertIsDisplayed()
    }
    
    @Test
    fun registrationScreen_accessibility_navigation() {
        // Given
        stateFlow.value = PasskeyState.Idle
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                PasskeyRegistrationScreen(
                    screenModel = screenModel,
                    onRegistrationComplete = {}
                )
            }
        }
        
        // Then - Check all interactive elements have content descriptions
        composeTestRule
            .onNodeWithContentDescription("Create passkey button")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithContentDescription("Learn more about passkeys")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithContentDescription("Skip passkey setup")
            .assertIsDisplayed()
    }
    
    @Test
    fun registrationScreen_errorRetry_resetsState() {
        // Given
        stateFlow.value = PasskeyState.Error(
            message = "Network error occurred",
            canRetry = true
        )
        
        // When
        composeTestRule.setContent {
            MangalaAppTheme {
                PasskeyRegistrationScreen(
                    screenModel = screenModel,
                    onRegistrationComplete = {}
                )
            }
        }
        
        composeTestRule
            .onNodeWithText("Try Again")
            .performClick()
        
        // Then
        verify { screenModel.retry() }
    }
}