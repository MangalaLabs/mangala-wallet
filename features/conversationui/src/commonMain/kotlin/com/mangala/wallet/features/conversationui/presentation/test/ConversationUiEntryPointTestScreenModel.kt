package com.mangala.wallet.features.conversationui.presentation.test

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.auth.AuthenticationFlowManager
import com.mangala.wallet.core.auth.SessionManager
import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ConversationUiEntryPointTestScreenModel(
    private val sessionManager: SessionManager,
    private val chatHistoryRepository: ChatHistoryRepository,
    private val authFlowManager: AuthenticationFlowManager
): BaseScreenModel() {
    
    sealed class NavigationEvent {
        data object NavigateToConversationUi : NavigationEvent()
        data object NavigateToSessionList : NavigationEvent()
        data object NavigateToSignIn : NavigationEvent()
    }

    // Since it's loaded in init, add replay so that events emitted before collector starts is not lost
    private val _navigationEvents = MutableSharedFlow<NavigationEvent?>(replay = 1)
    val navigationEvents: SharedFlow<NavigationEvent?> = _navigationEvents.asSharedFlow()

    init {
        checkSession()
    }
    
    private fun checkSession() {
        screenModelScope.launch {
            if (authFlowManager.authState.value is AuthState.Authenticated) {
                _navigationEvents.emit(NavigationEvent.NavigateToSessionList)
                return@launch
            }

            val session = sessionManager.loadSession()
            if (session != null) {
                _navigationEvents.emit(NavigationEvent.NavigateToSessionList)
            } else {
                _navigationEvents.emit(NavigationEvent.NavigateToSignIn)
            }
        }
    }
}