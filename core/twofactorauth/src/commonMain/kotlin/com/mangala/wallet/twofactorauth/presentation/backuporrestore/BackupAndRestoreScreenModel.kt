package com.mangala.wallet.twofactorauth.presentation.backuporrestore

import androidx.compose.ui.platform.ClipboardManager
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.twofactorauth.data.EncryptionUtils
import com.mangala.wallet.twofactorauth.domain.usecase.ExportBackupUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.Is2FAEnabledUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.VerifyBackupCodeUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class BackupAndRestoreScreenModel(
    private val exportBackupUseCase: ExportBackupUseCase,
    private val is2FAEnabledUseCase: Is2FAEnabledUseCase,
    private val verifyBackupCodeUseCase: VerifyBackupCodeUseCase
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow(BackupRestoreUiState())
    val uiState: StateFlow<BackupRestoreUiState> = _uiState.asStateFlow()

    override fun doOnComposableStarted() {
        super.doOnComposableStarted()
        // Check if 2FA is enabled when screen is started
        screenModelScope.launch {
            try {
                val is2FAEnabled = is2FAEnabledUseCase()
                if (!is2FAEnabled) {
                    _uiState.update { it.copy(errorMessage = "2FA is not enabled. Please set up 2FA first.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to check 2FA status: ${e.message}") }
            }
        }
    }

    fun setPassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password,
                passwordStrength = calculatePasswordStrength(password)
            )
        }
    }

    fun setBackupInputText(text: String) {
        _uiState.update { it.copy(backupInputText = text) }
    }

    private fun calculatePasswordStrength(password: String): Float {
        if (password.isEmpty()) return 0f

        var score = 0f

        // Length check
        when {
            password.length >= 12 -> score += 0.3f
            password.length >= 8 -> score += 0.2f
            password.length >= 6 -> score += 0.1f
        }

        // Character variety checks
        if (password.any { it.isDigit() }) score += 0.2f
        if (password.any { it.isLowerCase() }) score += 0.1f
        if (password.any { it.isUpperCase() }) score += 0.2f
        if (password.any { !it.isLetterOrDigit() }) score += 0.2f

        return score.coerceIn(0f, 1f)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun createBackup() {
        screenModelScope.launch {
            val password = _uiState.value.password
            if (password.length < 8) {
                _uiState.update { it.copy(errorMessage = "Password must be at least 8 characters") }
                return@launch
            }

            _uiState.update { it.copy(isOperationInProgress = true, errorMessage = null) }

            try {
                // Generate a random salt for key derivation
                val salt = EncryptionUtils.generateSecureRandomBytes(16)

                // Export the 2FA data
                val backupBytes = exportBackupUseCase(password)

                // Generate a secure encryption key from the user's password
                val encryptionKey = EncryptionUtils.deriveKeyFromPassword(password, salt)

                // Encrypt the backup data with AES-GCM
                val encryptedData = EncryptionUtils.encryptWithAesGcm(backupBytes, encryptionKey)

                // Combine salt and encrypted data
                val combinedData = ByteArray(salt.size + encryptedData.size)
                salt.copyInto(combinedData)
                encryptedData.copyInto(combinedData, salt.size)

                // Convert to Base64 for easy storage/transmission
                val backupString = Base64.encode(combinedData)

                _uiState.update {
                    it.copy(
                        backupData = backupString,
                        isOperationInProgress = false,
                        operationSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isOperationInProgress = false,
                        errorMessage = "Failed to create backup: ${e.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun restoreFromBackup() {
        screenModelScope.launch {
            val password = _uiState.value.password
            val backupText = _uiState.value.backupInputText

            if (password.isEmpty() || backupText.isEmpty()) {
                _uiState.update { it.copy(errorMessage = "Both password and backup data are required") }
                return@launch
            }

            _uiState.update { it.copy(isOperationInProgress = true, errorMessage = null) }

            try {
                // Decode the backup data from Base64
                val backupBytes = Base64.decode(backupText)

                // Use repository to restore the backup
                // This would be implemented in a real repository
                // For now, just validate with a simulated delay
                delay(1000) // Simulate work

                // Verify a backup code to test if the restore was successful
                val isSuccessful = verifyBackupCodeUseCase("000000") // Placeholder code

                if (isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isOperationInProgress = false,
                            operationSuccess = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isOperationInProgress = false,
                            errorMessage = "Failed to verify the restored backup."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isOperationInProgress = false,
                        errorMessage = "Failed to restore backup: ${e.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    fun resetOperationState() {
        _uiState.update {
            it.copy(
                operationSuccess = false,
                errorMessage = null
            )
        }
    }

    fun copyBackupToClipboard() {
//        _uiState.value.backupData?.let { backupData ->
//            try {
//                val clip = android.content.ClipData.newPlainText("Backup Data", backupData)
//                clipboardManager.setPrimaryClip(clip)
//
//                _uiState.update { it.copy(operationSuccess = true) }
//            } catch (e: Exception) {
//                _uiState.update { it.copy(errorMessage = "Failed to copy to clipboard: ${e.message}") }
//            }
//        }
    }

    fun saveBackupToFile() {
//        _uiState.value.backupData?.let { backupData ->
//            screenModelScope.launch {
//                try {
//                    val fileName = "2fa_backup_${System.currentTimeMillis()}.txt"
//                    fileManager.saveTextToFile(fileName, backupData)
//                    _uiState.update { it.copy(operationSuccess = true) }
//                } catch (e: Exception) {
//                    _uiState.update { it.copy(errorMessage = "Failed to save file: ${e.message}") }
//                }
//            }
//        }
    }

    fun backupToCloud() {
//        _uiState.value.backupData?.let { backupData ->
//            screenModelScope.launch {
//                try {
//                    _uiState.update { it.copy(isOperationInProgress = true) }
//                    cloudBackupService.uploadBackup(backupData)
//                    _uiState.update {
//                        it.copy(
//                            isOperationInProgress = false,
//                            operationSuccess = true
//                        )
//                    }
//                } catch (e: Exception) {
//                    _uiState.update {
//                        it.copy(
//                            isOperationInProgress = false,
//                            errorMessage = "Failed to upload to cloud: ${e.message}"
//                        )
//                    }
//                }
//            }
//        }
    }

    fun pickBackupFile() {
//        screenModelScope.launch {
//            try {
//                val fileContent = fileManager.pickAndReadTextFile()
//                if (fileContent != null) {
//                    _uiState.update { it.copy(backupInputText = fileContent) }
//                }
//            } catch (e: Exception) {
//                _uiState.update { it.copy(errorMessage = "Failed to read file: ${e.message}") }
//            }
//        }
    }

    fun scanQrCode() {
//        screenModelScope.launch {
//            try {
//                _uiState.update { it.copy(isOperationInProgress = true) }
//                // Launch QR scanner and get result
//                val qrResult = fileManager.scanQrCode()
//                if (qrResult != null) {
//                    _uiState.update {
//                        it.copy(
//                            backupInputText = qrResult,
//                            isOperationInProgress = false
//                        )
//                    }
//                } else {
//                    _uiState.update {
//                        it.copy(
//                            isOperationInProgress = false,
//                            errorMessage = "No QR code scanned or invalid format"
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        isOperationInProgress = false,
//                        errorMessage = "Failed to scan QR code: ${e.message}"
//                    )
//                }
//            }
//        }
    }
}

enum class BackupRestoreTab {
    BACKUP, RESTORE
}

data class BackupRestoreUiState(
    val password: String = "",
    val passwordStrength: Float = 0f,
    val isOperationInProgress: Boolean = false,
    val backupData: String? = null,
    val backupInputText: String = "",
    val operationSuccess: Boolean = false,
    val errorMessage: String? = null
)