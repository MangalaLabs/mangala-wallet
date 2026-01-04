package com.mangala.wallet.auth.presentation

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.auth.AuthenticationFlowManager
import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.auth.navigation.AuthNavigationHandler
import com.mangala.wallet.domain.datastore.usecases.CompleteOnboardingUseCase
import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.device.generateTempUserId
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class RegisterScreenModel(
    private val authFlowManager: AuthenticationFlowManager,
    private val navigationHandler: AuthNavigationHandler,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : BaseScreenModel() {
    
    private val _showHelpDialog = MutableStateFlow(false)
    val showHelpDialog: StateFlow<Boolean> = _showHelpDialog.asStateFlow()
    
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _isEmailValid = MutableStateFlow(false)
    val isEmailValid: StateFlow<Boolean> = _isEmailValid.asStateFlow()

    private val _showValidIcon = MutableStateFlow(false)
    val showValidIcon: StateFlow<Boolean> = _showValidIcon.asStateFlow()

    private val _isValidating = MutableStateFlow(false)
    val isValidating: StateFlow<Boolean> = _isValidating.asStateFlow()

    private val _isApiError = MutableStateFlow(false)
    val isApiError: StateFlow<Boolean> = _isApiError.asStateFlow()

    val authState: StateFlow<AuthState> = authFlowManager.authState

    private var validationJob: Job? = null
    
    fun updateEmail(email: String) {
        _email.value = email
        // Clear API error flag when user starts typing
        if (_isApiError.value) {
            _isApiError.value = false
        }
        validateEmailRealtime(email)
    }

    suspend fun createPasskey() {
        val currentEmail = _email.value.trim()
        
        if (!validateInputs(currentEmail)) return

        try {
            val userId = currentEmail
            val userName = generateTempUserId()
            val userDisplayName = currentEmail.substringBefore("@")

            MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.SIGN_UP_STARTED)

            val registrationSuccess = authFlowManager.registerPasskey(
                userId = userId,
                userName = userName,
                userDisplayName = userDisplayName
            )
            
            if (registrationSuccess) {
                MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.SIGN_UP_COMPLETED)
                completeOnboardingUseCase()
                // Registration successful - AuthenticationFlowManager should set state to Authenticated
            } else {
                MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.SIGN_UP_ERROR)
                _emailError.value = "Failed to create passkey. Please try again."
                _showValidIcon.value = false
                _isApiError.value = true
                authFlowManager.resetAuthState()
            }
        } catch (e: Exception) {
            // Handle user cancellation gracefully - don't show error
            if (e is PasskeyException.UserCancelled) {
                Napier.d("RegisterScreenModel: User cancelled passkey creation")
                authFlowManager.resetAuthState()
                return
            }

            MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.SIGN_UP_ERROR)
            Napier.e("RegisterScreenModel: Exception during passkey creation", e)

            val errorMessage = when (e) {
                is PasskeyException.EmailConflict -> {
                    "This email address is already registered. Please use a different email or sign in instead."
                }
                is PasskeyException.NotSupported -> {
                    "Passkey authentication is not supported on this device."
                }
                is PasskeyException.Timeout -> {
                    "Passkey operation timed out. Please try again."
                }
                is PasskeyException.NetworkError -> {
                    "Network error occurred. Please check your connection and try again."
                }
                is PasskeyException.PasskeyDecryptionFailed -> {
                    "Registration failed due to an unexpected error. Please try again."
                }
                else -> "Registration failed: ${e.message}"
            }

            _emailError.value = errorMessage
            _showValidIcon.value = false
            _isApiError.value = true
            authFlowManager.resetAuthState()
        }
    }
    
    private fun validateInputs(email: String): Boolean {
        var isValid = true
        
        if (email.isEmpty()) {
            _emailError.value = "Email is required"
            isValid = false
        } else if (!isValidEmail(email)) {
            _emailError.value = "Please enter a valid email address"
            isValid = false
        }
        
        return isValid
    }

    private fun validateEmailRealtime(email: String) {
        // Cancel previous validation job
        validationJob?.cancel()

        // Clear error immediately when user types
        if (_emailError.value != null) {
            _emailError.value = null
        }

        // Hide valid icon while typing
        _showValidIcon.value = false

        // Don't validate empty or very short input
        if (email.length < 3) {
            _isEmailValid.value = false
            _isValidating.value = false
            return
        }

        // Set validating state
        _isValidating.value = true

        // Debounce validation by 500ms
        validationJob = screenModelScope.launch {
            delay(500)

            val error = getEmailValidationError(email)
            _emailError.value = error
            _isEmailValid.value = error == null
            _showValidIcon.value = error == null && email.isNotEmpty()
            _isValidating.value = false
        }
    }

    private fun getEmailValidationError(email: String): String? {
        return when {
            email.isEmpty() -> "Email is required"
            !email.contains("@") -> "Email must contain '@'"
            email.count { it == '@' } > 1 -> "Email can only contain one '@'"
            email.startsWith("@") -> "Email cannot start with '@'"
            !email.contains(".") -> "Email must contain a domain (e.g., .com)"
            email.indexOf("@") > email.lastIndexOf(".") -> "Invalid email format"
            email.contains("..") -> "Email cannot contain consecutive dots"
            email.contains(" ") -> "Email cannot contain spaces"
            email.length < 3 -> "Email is too short"
            !isValidEmail(email) -> "Please enter a valid email address"
            else -> null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false

        // Enhanced email regex pattern
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        return emailRegex.matches(email) &&
                email.count { it == '@' } == 1 && // Exactly one @
                !email.contains("..") && // No consecutive dots
                !email.startsWith(".") && // No leading dot
                !email.endsWith(".") // No trailing dot
    }

    fun resetState() {
        screenModelScope.launch {
            authFlowManager.logout()
        }
    }
    
    fun showPasskeyHelp() {
        _showHelpDialog.value = true
    }
    
    fun hidePasskeyHelp() {
        _showHelpDialog.value = false
    }
    
    fun navigateToConversationUi() {
        navigationHandler.navigateToConversationUi()
    }
}