package com.mangala.wallet.features.addressbook.presentation.privacy

import com.mangala.wallet.features.addressbook.data.model.setting.UserSettingEntity
import com.mangala.wallet.features.addressbook.domain.usecase.setting.GetCurrentUserSettingUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.setting.UpdateCurrentSettingUseCase
import com.mangala.wallet.features.addressbook.domain.repository.setting.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for PrivacyModeViewModel
 * 
 * Test Coverage:
 * - Initial state loading
 * - Privacy mode toggle functionality
 * - Error handling
 * - Reactive state updates
 */
class PrivacyModeViewModelTest {
    
    // Mock dependencies
    private val mockUserSettings = UserSettingEntity.createDefault("test")
    private val settingsFlow = MutableStateFlow<UserSettingEntity?>(mockUserSettings)
    
    private val mockGetCurrentUserSettingUseCase = object : GetCurrentUserSettingUseCase {
        override suspend fun invoke(): UserSettingEntity? = settingsFlow.value
    }
    
    private val mockUpdateCurrentSettingUseCase = object : UpdateCurrentSettingUseCase {
        override suspend fun invoke(settings: UserSettingEntity): Result<Boolean> {
            settingsFlow.value = settings
            return Result.success(true)
        }
    }
    
    private val mockSettingsRepository = object : SettingsRepository {
        override suspend fun getUserSettings(id: String): UserSettingEntity? = settingsFlow.value
        override fun observeUserSettings(id: String) = settingsFlow
        override suspend fun saveUserSettings(settings: UserSettingEntity): Result<Boolean> = Result.success(true)
        override suspend fun togglePrivacyMode(enabled: Boolean): Boolean = true
        // Other methods not needed for these tests...
        override suspend fun toggleBiometricAuth(enabled: Boolean): Boolean = true
        override suspend fun toggleTwoFactorAuth(enabled: Boolean): Boolean = true
        override suspend fun toggleSync(enabled: Boolean): Boolean = true
        override suspend fun setDefaultSortOption(sortOption: String): Boolean = true
        override suspend fun setTheme(theme: String): Boolean = true
        override suspend fun addSafeZone(name: String, latitude: Double, longitude: Double, radiusMeters: Double): Boolean = true
        override suspend fun removeSafeZone(zoneId: String): Boolean = true
        override suspend fun getCurrentAppVersion() = null
        override suspend fun saveAppVersion(appVersion: com.mangala.wallet.features.addressbook.data.model.setting.AppVersionEntity): Boolean = true
        override suspend fun needsDatabaseMigration(): Boolean = false
        override suspend fun getOfflineQueue() = emptyList<com.mangala.wallet.features.addressbook.data.model.setting.OfflineQueueEntity>()
        override suspend fun addToOfflineQueue(offlineQueue: com.mangala.wallet.features.addressbook.data.model.setting.OfflineQueueEntity): Boolean = true
        override suspend fun removeFromOfflineQueue(id: String): Boolean = true
        override suspend fun updateOfflineQueueItem(offlineQueue: com.mangala.wallet.features.addressbook.data.model.setting.OfflineQueueEntity): Boolean = true
        override suspend fun countOfflineQueueItems(): Int = 0
        override suspend fun incrementOfflineQueueAttempts(id: String): Boolean = true
    }
    
    private fun createViewModel() = PrivacyModeViewModel(
        getCurrentUserSettingUseCase = mockGetCurrentUserSettingUseCase,
        updateCurrentSettingUseCase = mockUpdateCurrentSettingUseCase,
        settingsRepository = mockSettingsRepository
    )
    
    @Test
    fun `initial state should be false when privacy mode is disabled`() = runTest {
        // Given
        settingsFlow.value = mockUserSettings.copy(privacyModeEnabled = false)
        
        // When
        val viewModel = createViewModel()
        
        // Then
        assertFalse(viewModel.isEnabled.first())
        assertFalse(viewModel.isPrivacyModeEnabled())
    }
    
    @Test
    fun `initial state should be true when privacy mode is enabled`() = runTest {
        // Given
        settingsFlow.value = mockUserSettings.copy(privacyModeEnabled = true)
        
        // When
        val viewModel = createViewModel()
        
        // Then
        assertTrue(viewModel.isEnabled.first())
        assertTrue(viewModel.isPrivacyModeEnabled())
    }
    
    @Test
    fun `toggle should change privacy mode state`() = runTest {
        // Given
        settingsFlow.value = mockUserSettings.copy(privacyModeEnabled = false)
        val viewModel = createViewModel()
        
        // When
        val result = viewModel.toggle()
        
        // Then
        assertTrue(result)
        assertTrue(viewModel.isEnabled.first())
    }
    
    @Test
    fun `setPrivacyMode should update state correctly`() = runTest {
        // Given
        settingsFlow.value = mockUserSettings.copy(privacyModeEnabled = false)
        val viewModel = createViewModel()
        
        // When
        val result = viewModel.setPrivacyMode(true)
        
        // Then
        assertTrue(result)
        assertTrue(viewModel.isEnabled.first())
    }
    
    @Test
    fun `getPrivacyStateMessage should return correct message for enabled state`() = runTest {
        // Given
        settingsFlow.value = mockUserSettings.copy(privacyModeEnabled = true)
        val viewModel = createViewModel()
        
        // When
        val message = viewModel.getPrivacyStateMessage()
        
        // Then
        assertEquals("Privacy mode is enabled - sensitive addresses are hidden", message)
    }
    
    @Test
    fun `getPrivacyStateMessage should return correct message for disabled state`() = runTest {
        // Given
        settingsFlow.value = mockUserSettings.copy(privacyModeEnabled = false)
        val viewModel = createViewModel()
        
        // When
        val message = viewModel.getPrivacyStateMessage()
        
        // Then
        assertEquals("Privacy mode is disabled - all addresses are visible", message)
    }
    
    @Test
    fun `clearError should reset error state`() = runTest {
        // Given
        val viewModel = createViewModel()
        
        // When
        viewModel.clearError()
        
        // Then
        assertEquals(null, viewModel.error.first())
    }
}

// Helper interfaces for mocking (since original use cases are classes)
private interface GetCurrentUserSettingUseCase {
    suspend operator fun invoke(): UserSettingEntity?
}

private interface UpdateCurrentSettingUseCase {
    suspend operator fun invoke(settings: UserSettingEntity): Result<Boolean>
}