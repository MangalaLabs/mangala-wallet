package com.mangala.wallet.auth.presentation

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.auth.AuthenticationFlowManager
import com.mangala.wallet.core.auth.domain.model.AuthState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthScreenModel(
    private val authFlowManager: AuthenticationFlowManager
) : StateScreenModel<AuthScreenState>(AuthScreenState()) {
    
    init {
        screenModelScope.launch {
            authFlowManager.authState.collect { authState ->
                mutableState.update { currentState ->
                    currentState.copy(
                        authState = authState,
                        isLoading = authState is AuthState.Loading
                    )
                }
            }
        }
    }
    
    fun authenticate() {
        screenModelScope.launch {
            authFlowManager.authenticate()
        }
    }
    
    fun logout() {
        screenModelScope.launch {
            authFlowManager.logout()
        }
    }
}

data class AuthScreenState(
    val authState: AuthState = AuthState.Initial,
    val isLoading: Boolean = false,
    val isRegistering: Boolean = false,
    val registrationSuccess: Boolean = false,
    val registrationError: String? = null
)