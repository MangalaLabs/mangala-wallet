package com.mangala.wallet.auth.presentation.signin

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.auth.AuthenticationFlowManager
import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.auth.navigation.AuthNavigationHandler
import com.mangala.wallet.domain.datastore.usecases.CompleteOnboardingUseCase
import com.mangala.wallet.domain.portfolio.usecases.CreatePortfolioUseCase
import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInScreenModel(
    private val authFlowManager: AuthenticationFlowManager,
    private val navigationHandler: AuthNavigationHandler,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val createPortfolioUseCase: CreatePortfolioUseCase
) : BaseScreenModel() {
    
    private val _showHelpDialog = MutableStateFlow(false)
    val showHelpDialog: StateFlow<Boolean> = _showHelpDialog.asStateFlow()
    
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()
    
    private val _navigateToRegistration = MutableStateFlow(false)
    val navigateToRegistration: StateFlow<Boolean> = _navigateToRegistration.asStateFlow()
    
    val authState: StateFlow<AuthState> = authFlowManager.authState
    
    fun authenticateWithoutEmail() {
        MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.SIGN_IN_STARTED)

        screenModelScope.launch {
            _emailError.value = null
            
            val result = authFlowManager.authenticateWithPasskey(null)

            if (result.isSuccess) {
                MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.SIGN_IN_COMPLETED)
                completeOnboardingUseCase()
                createPortfolioUseCase()

                navigationHandler.navigateToConversationUi()
            } else {
                authFlowManager.resetAuthState()
                
                val exception = result.exceptionOrNull()
                _emailError.value = when (exception) {
                    is PasskeyException.UserCancelled -> {
                        return@launch
                    }
                    is PasskeyException.NotSupported -> {
                        MangalaAnalytics.trackEvent(
                            MangalaAnalytics.EventName.SIGN_IN_ERROR,
                            mapOf(MangalaAnalytics.EventParam.ERROR_TYPE to MangalaAnalytics.EventParamValue.SIGN_IN_ERROR_PASSKEY_NOT_SUPPORTED)
                        )
                        "Passkey not supported on this device"
                    }
                    is PasskeyException.CredentialNotFound -> {
                        MangalaAnalytics.trackEvent(
                            MangalaAnalytics.EventName.SIGN_IN_ERROR,
                            mapOf(MangalaAnalytics.EventParam.ERROR_TYPE to MangalaAnalytics.EventParamValue.SIGN_IN_ERROR_CREDENTIAL_NOT_FOUND)
                        )
                        "No passkey found. Please register first."
                    }
                    else -> {
                        MangalaAnalytics.trackEvent(
                            MangalaAnalytics.EventName.SIGN_IN_ERROR,
                            mapOf(MangalaAnalytics.EventParam.ERROR_TYPE to MangalaAnalytics.EventParamValue.SIGN_IN_ERROR_OTHER_ERROR)
                        )
                        exception?.message ?: "Authentication failed. Please try again."
                    }
                }
            }
        }
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
    
    fun onNavigationHandled() {
        _navigateToRegistration.value = false
    }
}