package com.mangala.wallet.twofactorauth.presentation.setup

import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.twofactorauth.domain.usecase.GetAuthStateUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.Is2FAEnabledUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.Setup2FAUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.VerifyCodeUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class TwoFactorAuthenticationSetupScreenModel(
    private val setup2FAUseCase: Setup2FAUseCase,
    private val is2FAEnabledUseCase: Is2FAEnabledUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase
) : BaseScreenModel(), KoinComponent {

    private val _uiState = MutableStateFlow(TwoFactorSetupUiState())
    val uiState: StateFlow<TwoFactorSetupUiState> = _uiState.asStateFlow()

    override fun doOnComposableStarted() {
        super.doOnComposableStarted()

        // Check if 2FA is already enabled
        lifecycleScope.launch {
            val is2FAEnabled = is2FAEnabledUseCase()
            if (is2FAEnabled) {
                // 2FA is already set up, handle this case (maybe navigate back or show a message)
                // For now, we'll just continue with the setup flow
            }

            // Start the setup process if we're at the introduction step
            if (uiState.value.currentStep == TwoFactorSetupStep.INTRODUCTION) {
                // We'll delay actually generating the TOTP secret until the user moves to the next step
            }
        }
    }

    fun onStepChanged(step: TwoFactorSetupStep) {
        when (step) {
            TwoFactorSetupStep.QR_AND_SECRET -> {
                if (_uiState.value.totpSecret.isEmpty()) {
                    generateTotpSecret()
                }
            }

            TwoFactorSetupStep.BACKUP_CODES -> {
                // Already handled when generating TOTP secret
            }

            TwoFactorSetupStep.CONFIRMATION -> {
                // Reset verification state when entering confirmation step
                _uiState.update {
                    it.copy(
                        currentEnteredOtp = "",
                        verificationError = null,
                        isVerifying = false
                    )
                }
            }

            else -> { /* No special handling needed for other steps */
            }
        }

        _uiState.update { it.copy(currentStep = step) }
    }

    private fun generateTotpSecret() {
        lifecycleScope.launch {
            try {
                // In a real app, get the wallet address from repository/domain layer
                val walletAddress = "example_wallet_address"
                val setupResult = setup2FAUseCase(walletAddress)

                _uiState.update {
                    it.copy(
                        totpSecret = setupResult.secret,
                        qrCodeUri = setupResult.qrCodeUri,
                        backupCodes = setupResult.backupCodes
                    )
                }
            } catch (e: Exception) {
                // Handle error, maybe navigate back or show an error message
                println("Error generating TOTP secret: ${e.message}")
            }
        }
    }

    fun copySecretToClipboard() {
        // In a real app, this would access the clipboard via a platform-specific service
        // For now, let's assume it works
        println("Secret copied to clipboard: ${_uiState.value.totpSecret}")
    }

    fun saveBackupCodesAsFile() {
        lifecycleScope.launch {
            try {
                // In a real app, you would use a proper file service to save the codes
                // For now, let's just simulate it
                val backupCodesText = _uiState.value.backupCodes.joinToString("\n")
                println("Backup codes saved to file: $backupCodesText")
            } catch (e: Exception) {
                println("Error saving backup codes: ${e.message}")
            }
        }
    }

    fun sendBackupCodesByEmail() {
        // In a real app, this would integrate with email services or intent
        println("Sending backup codes by email")
    }

    fun setCurrentEnteredOtp(otp: String) {
        _uiState.update {
            it.copy(
                currentEnteredOtp = otp,
                verificationError = null
            )
        }
    }

    fun verifyInitialOtp() {
        val currentOtp = _uiState.value.currentEnteredOtp

        if (currentOtp.length != 6) {
            _uiState.update { it.copy(verificationError = "Please enter a 6-digit code") }
            return
        }

        _uiState.update { it.copy(isVerifying = true, verificationError = null) }

        lifecycleScope.launch {
            try {
                val isValid = verifyCodeUseCase(currentOtp)

                if (isValid) {
                    _uiState.update {
                        it.copy(
                            isVerifying = false,
                            currentStep = TwoFactorSetupStep.COMPLETED
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isVerifying = false,
                            verificationError = "Invalid verification code. Please try again."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isVerifying = false,
                        verificationError = "Error verifying code: ${e.message}"
                    )
                }
            }
        }
    }

    fun completeTwoFactorSetup() {
        _uiState.update { it.copy(isSetupComplete = true) }
    }
}