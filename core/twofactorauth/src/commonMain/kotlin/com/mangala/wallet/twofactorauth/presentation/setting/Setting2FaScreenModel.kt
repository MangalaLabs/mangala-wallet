package com.mangala.wallet.twofactorauth.presentation.setting

import com.mangala.wallet.twofactorauth.domain.usecase.Disable2FAUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.ExportBackupUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.GetAuthStateUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.ImportBackupUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.Is2FAEnabledUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class Setting2FaScreenModel(
    private val is2FAEnabledUseCase: Is2FAEnabledUseCase,
    private val disable2FAUseCase: Disable2FAUseCase,
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase
) : BaseScreenModel(), KoinComponent {

    private val _uiState = MutableStateFlow(Setting2FaUiState())
    val uiState: StateFlow<Setting2FaUiState> = _uiState.asStateFlow()

    // Used to store confirmation code for disabling 2FA
    private var disableConfirmationCode: String = ""

    override fun doOnComposableStarted() {
        super.doOnComposableStarted()

        // Check if 2FA is enabled
        check2FAStatus()
    }

    private fun check2FAStatus() {
        lifecycleScope.launch {
            try {
                val isEnabled = is2FAEnabledUseCase()
                _uiState.update { it.copy(is2faEnabled = isEnabled) }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Navigation functions

    fun navigateToBackupRestore() {
        // This would use your navigation system to go to backup/restore screen
    }

    fun navigateToBackupCodes() {
        // Navigate to backup codes screen
    }

    fun navigateToHelp() {
        // Navigate to help screen
    }

    // 2FA Disable functionality

    fun showDisableDialog() {
        _uiState.update { it.copy(showDisableDialog = true) }
    }

    fun hideDisableDialog() {
        _uiState.update { it.copy(showDisableDialog = false) }
    }

    fun setDisableConfirmationCode(code: String) {
        disableConfirmationCode = code
    }

    fun disable2FA() {
        lifecycleScope.launch {
            try {
                val result = disable2FAUseCase(disableConfirmationCode)

                if (result) {
                    _uiState.update {
                        it.copy(
                            is2faEnabled = false,
                            disableResult = "2FA has been successfully disabled"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            disableResult = "Failed to disable 2FA. Invalid confirmation code."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        disableResult = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    // Backup functionality

    fun exportBackup(password: String) {
        lifecycleScope.launch {
            try {
                val backupData = exportBackupUseCase(password)
                // In a real app, you would save this data or share it
                _uiState.update {
                    it.copy(
                        backupExported = true
                    )
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun importBackup(data: ByteArray, password: String) {
        lifecycleScope.launch {
            try {
                val result = importBackupUseCase(data, password)
                if (result) {
                    // Refresh 2FA status
                    check2FAStatus()
                    _uiState.update {
                        it.copy(
                            backupImported = true
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

data class Setting2FaUiState(
    val is2faEnabled: Boolean = false,
    val showDisableDialog: Boolean = false,
    val disableResult: String? = null,
    val backupExported: Boolean = false,
    val backupImported: Boolean = false
)