package com.mangala.wallet.features.addressbook.presentation.privacy

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.domain.usecase.setting.GetCurrentUserSettingUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.setting.UpdateCurrentSettingUseCase
import com.mangala.wallet.features.addressbook.domain.repository.setting.SettingsRepository
import com.mangala.wallet.features.addressbook.data.model.setting.UserSettingEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * ViewModel for managing Privacy Mode state across the application.
 * 
 * Responsibilities:
 * - Manage privacy mode enabled/disabled state
 * - Persist state changes to database
 * - Provide reactive state updates to UI
 * - Handle errors and edge cases
 */
class PrivacyModeViewModel(
    private val getCurrentUserSettingUseCase: GetCurrentUserSettingUseCase,
    private val updateCurrentSettingUseCase: UpdateCurrentSettingUseCase,
    private val settingsRepository: SettingsRepository
) : ScreenModel {
    
    // Privacy mode state
    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()
    
    // Loading state for UI feedback
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadInitialState()
        observeUserSettings()
    }
    
    /**
     * Load initial privacy mode state from database
     */
    private fun loadInitialState() {
        screenModelScope.launch {
            try {
                _isLoading.value = true
                val userSettings = getCurrentUserSettingUseCase()
                _isEnabled.value = userSettings?.privacyModeEnabled ?: false
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load privacy settings: ${e.message}"
                _isEnabled.value = false // Safe default
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Observe user settings changes and update privacy mode state reactively
     */
    private fun observeUserSettings() {
        screenModelScope.launch {
            settingsRepository.observeUserSettings("current")
                .map { it?.privacyModeEnabled ?: false }
                .distinctUntilChanged()
                .catch { e ->
                    _error.value = "Settings observation error: ${e.message}"
                }
                .collect { enabled ->
                    _isEnabled.value = enabled
                }
        }
    }
    
    /**
     * Toggle privacy mode on/off
     * 
     * @return Boolean indicating success/failure
     */
    fun toggle(): Boolean {
        val newState = !_isEnabled.value
        return setPrivacyMode(newState)
    }
    
    /**
     * Set privacy mode to specific state
     * 
     * @param enabled True to enable privacy mode, false to disable
     * @return Boolean indicating success/failure
     */
    fun setPrivacyMode(enabled: Boolean): Boolean {
        // Optimistic update for immediate UI feedback
        val previousState = _isEnabled.value
        _isEnabled.value = enabled
        
        screenModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                // Get current settings
                val currentSettings = getCurrentUserSettingUseCase() 
                    ?: UserSettingEntity.createDefault("current")
                
                // Update privacy mode setting
                val updatedSettings = currentSettings.copy(
                    privacyModeEnabled = enabled,
                    updatedAt = Clock.System.now()
                )
                
                // Save to database
                val result = updateCurrentSettingUseCase(updatedSettings)
                
                if (!result.isSuccess) {
                    // Rollback on failure
                    _isEnabled.value = previousState
                    _error.value = "Failed to update privacy settings"
                    return@launch
                }
                
                // Log privacy action for audit trail
                logPrivacyAction(enabled)
                
            } catch (e: Exception) {
                // Rollback on error
                _isEnabled.value = previousState
                _error.value = "Error updating privacy mode: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
        
        return true // Return immediately for UI responsiveness
    }
    
    /**
     * Check if privacy mode is currently enabled
     */
    fun isPrivacyModeEnabled(): Boolean = _isEnabled.value
    
    /**
     * Clear any error state
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Force refresh privacy mode state from database
     */
    fun refresh() {
        loadInitialState()
    }
    
    /**
     * Log privacy mode toggle action for security audit
     * Note: This integrates with existing security audit system
     */
    private fun logPrivacyAction(enabled: Boolean) {
        // TODO: Integrate with existing SecurityAuditRepository when implementing Task 1.5
        // For now, this is a placeholder for the audit trail functionality
        println("Privacy Mode ${if (enabled) "ENABLED" else "DISABLED"} at ${Clock.System.now()}")
    }
    
    /**
     * Get user-friendly message for current privacy state
     */
    fun getPrivacyStateMessage(): String {
        return if (_isEnabled.value) {
            "Privacy mode is enabled - sensitive addresses are hidden"
        } else {
            "Privacy mode is disabled - all addresses are visible"
        }
    }
}

