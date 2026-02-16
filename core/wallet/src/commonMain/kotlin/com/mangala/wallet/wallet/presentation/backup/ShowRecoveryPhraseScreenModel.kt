package com.mangala.wallet.wallet.presentation.backup

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletByIdUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class ShowRecoveryPhraseUiState(
    val walletId: String = "",
    val walletName: String = "",
    val recoveryPhrase: List<String> = emptyList(),
    val verificationPositions: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ShowRecoveryPhraseScreenModel(
    private val walletId: String?,
    private val getWalletByIdUseCase: GetWalletByIdUseCase,
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val walletRepository: WalletRepository
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow(ShowRecoveryPhraseUiState())
    val uiState: StateFlow<ShowRecoveryPhraseUiState> = _uiState.asStateFlow()

    init {
        loadRecoveryPhrase()
    }

    private fun loadRecoveryPhrase() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // If walletId is provided, get wallet by ID; otherwise, get selected wallet
                val wallet = if (!walletId.isNullOrEmpty()) {
                    getWalletByIdUseCase(walletId)
                } else {
                    getSelectedWalletUseCase()
                }

                if (wallet == null) {
                    _uiState.value = _uiState.value.copy(
                        recoveryPhrase = emptyList(),
                        verificationPositions = emptyList(),
                        isLoading = false,
                        error = "Wallet not found"
                    )
                    return@launch
                }

                val wordsString = wallet.words ?: ""
                val recoveryPhrase = if (wordsString.isNotEmpty()) {
                    wordsString.split(" ")
                } else {
                    emptyList()
                }
                val verificationPositions = _uiState.value.verificationPositions.ifEmpty {
                    pickVerificationPositions()
                }

                _uiState.value = _uiState.value.copy(
                    walletId = wallet.id,
                    walletName = wallet.name ?: "",
                    recoveryPhrase = recoveryPhrase,
                    verificationPositions = verificationPositions,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    recoveryPhrase = emptyList(),
                    verificationPositions = emptyList(),
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun onContinueClick() {
        screenModelScope.launch {
            try {
                val currentWalletId = _uiState.value.walletId
                if (currentWalletId.isNotEmpty()) {
                    walletRepository.markWalletAsBackedUp(currentWalletId)
                }
            } catch (e: Exception) {
                // Non-critical: backup status is a UX convenience, not a blocker
                e.printStackTrace()
            }
        }
    }

    private fun pickVerificationPositions(random: Random = Random.Default): List<Int> {
        val firstGroup = (1..4).random(random)
        val secondGroup = (5..8).random(random)
        val thirdGroup = (9..12).random(random)
        return listOf(firstGroup, secondGroup, thirdGroup).shuffled(random)
    }
}
