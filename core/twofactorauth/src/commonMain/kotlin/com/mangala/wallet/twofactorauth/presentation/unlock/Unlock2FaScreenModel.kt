package com.mangala.wallet.twofactorauth.presentation.unlock

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.twofactorauth.domain.usecase.AuthenticateTransactionUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.GetAuthStateUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.Is2FAEnabledUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.VerifyCodeUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class Unlock2FaScreenModel(
    private val onUnlockSuccess: () -> Unit,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase
) : BaseScreenModel(), KoinComponent {

    private val _uiState = MutableStateFlow(Unlock2FaUiState())
    val uiState: StateFlow<Unlock2FaUiState> = _uiState.asStateFlow()

    override fun doOnComposableStarted() {
        super.doOnComposableStarted()
        screenModelScope.launch {
            getAuthStateUseCase().collect { isAuthenticated ->
                if (isAuthenticated) {
                    onUnlockSuccess()
                }
            }
        }
    }

    fun setCurrentEnteredOtp(otp: String) {
        _uiState.update {
            it.copy(
                currentEnteredOtp = otp,
                verificationError = null
            )
        }
    }

    fun setCurrentEnteredBackupCode(code: String) {
        _uiState.update {
            it.copy(
                currentEnteredBackupCode = code,
                verificationError = null
            )
        }
    }

    fun setRememberDevice(remember: Boolean) {
        _uiState.update { it.copy(rememberDevice = remember) }
    }

    fun verifyOtp() {
        val otpCode = uiState.value.currentEnteredOtp
        if (otpCode.length < 6) return

        _uiState.update { it.copy(isVerifying = true) }

        screenModelScope.launch {
            try {
                val success = verifyCodeUseCase(otpCode)

                if (success) {
                    handleSuccessfulVerification()
                } else {
                    handleFailedVerification("Invalid verification code. Please try again.")
                }
            } catch (e: Exception) {
                handleFailedVerification("Verification failed: ${e.message}")
            } finally {
                _uiState.update { it.copy(isVerifying = false) }
            }
        }
    }

    fun verifyBackupCode() {
        val backupCode = uiState.value.currentEnteredBackupCode
        if (backupCode.isEmpty()) return

        _uiState.update { it.copy(isVerifying = true) }

        screenModelScope.launch {
            try {
                // Assuming backup codes use the same verification system
                // You might need to create a separate use case for backup codes
                val success = verifyCodeUseCase(backupCode)

                if (success) {
                    handleSuccessfulVerification()
                } else {
                    handleFailedVerification("Invalid backup code. Please try again.")
                }
            } catch (e: Exception) {
                handleFailedVerification("Verification failed: ${e.message}")
            } finally {
                _uiState.update { it.copy(isVerifying = false) }
            }
        }
    }

    private fun handleSuccessfulVerification() {
        // Call the success callback
        onUnlockSuccess()
    }

    private fun handleFailedVerification(errorMessage: String) {
        _uiState.update {
            it.copy(
                verificationError = errorMessage,
                remainingAttempts = it.remainingAttempts - 1,
                currentEnteredOtp = "",
                currentEnteredBackupCode = ""
            )
        }
    }
}

data class Unlock2FaUiState(
    val currentEnteredOtp: String = "",
    val currentEnteredBackupCode: String = "",
    val isVerifying: Boolean = false,
    val verificationError: String? = null,
    val remainingAttempts: Int = 5,
    val rememberDevice: Boolean = false
)