package com.mangala.wallet.features.conversationui.presentation.sessionlist

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.core.auth.SessionManager
import com.mangala.wallet.features.conversationui.domain.model.ConversationSession
import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ConversationSessionListScreenModel(
    private val chatHistoryRepository: ChatHistoryRepository,
    private val sessionManager: SessionManager
) : BaseScreenModel() {

    sealed class NavigationEvent {
        data class NavigateToConversation(val sessionId: String) : NavigationEvent()
        data object NavigateToNewConversation : NavigationEvent()
    }

    data class UiState(
        val sessions: List<ConversationSession> = emptyList(),
        val currentSessionId: String? = null,
        val isLoading: Boolean = true,
        val error: String? = null
    )

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadSessions()
        observeCurrentSession()
    }

    private var sessionsJob: kotlinx.coroutines.Job? = null
    
    private fun loadSessions() {
        // Cancel any existing collection
        sessionsJob?.cancel()
        
        sessionsJob = screenModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val userId = getUserId()
                println("[SessionListScreenModel] Loading sessions for user: $userId")
                
                // Get all sessions for the user
                chatHistoryRepository.getAllSessions(userId)
                    .catch { e ->
                        println("[SessionListScreenModel] Error loading sessions: ${e.message}")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = e.message
                            )
                        }
                    }
                    .collect { sessions ->
                        println("[SessionListScreenModel] Received ${sessions.size} sessions")
                        val sortedSessions = sessions.sortedByDescending { session -> 
                            session.lastUpdatedTime 
                        }
                        
                        _uiState.update {
                            it.copy(
                                sessions = sortedSessions,
                                currentSessionId = null, // No active session tracking
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                println("[SessionListScreenModel] Exception loading sessions: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private var currentSessionJob: kotlinx.coroutines.Job? = null
    
    private fun observeCurrentSession() {
        // Cancel any existing observation
        currentSessionJob?.cancel()
        
        // Don't observe current session in the list screen - it auto-creates sessions
        // We'll just track the current session from the sessions list
    }

    fun navigateToSession(sessionId: String) {
        screenModelScope.launch {
            MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.CONVERSATION_OPENED)

            _navigationEvents.emit(NavigationEvent.NavigateToConversation(sessionId))
        }
    }

    fun createNewSession() {
        screenModelScope.launch {
            MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.CONVERSATION_CREATED)

            _navigationEvents.emit(NavigationEvent.NavigateToNewConversation)
        }
    }

    fun deleteSession(sessionId: String) {
        screenModelScope.launch {
            try {
                println("[SessionListScreenModel] Starting delete for session: $sessionId")
                
                // Check if this is the current session
                val currentSessionId = _uiState.value.currentSessionId
                val sessions = _uiState.value.sessions
                
                println("[SessionListScreenModel] Current session: $currentSessionId, Total sessions: ${sessions.size}")
                
                // Delete the session
                chatHistoryRepository.deleteSession(sessionId)
                
                println("[SessionListScreenModel] Session deleted from repository")
                
                // No need to handle active sessions anymore
                if (sessions.size == 1) {
                    // This is the only session - don't create a new one automatically
                    println("[SessionListScreenModel] Deleted the only session")
                    // The user can create a new one manually if needed
                }
                
                // Get the latest sessions after delete
                println("[SessionListScreenModel] Getting latest sessions after delete")
                val userId = getUserId()
                val latestSessions = chatHistoryRepository.getAllSessions(userId).first()
                println("[SessionListScreenModel] Got ${latestSessions.size} sessions after delete")
                
                val sortedSessions = latestSessions.sortedByDescending { session -> 
                    session.lastUpdatedTime 
                }
                
                _uiState.update {
                    it.copy(
                        sessions = sortedSessions,
                        currentSessionId = null // No active session tracking
                    )
                }
                
            } catch (e: Exception) {
                println("[SessionListScreenModel] Error deleting session: ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(error = "Failed to delete session: ${e.message}")
                }
            }
        }
    }

    fun selectSession(sessionId: String) {
        // No longer needed - sessions are selected by navigation
    }

    private fun getUserId(): String {
        // Get user ID from session manager
        return sessionManager.loadSession()?.userId ?: "default_user"
    }
}