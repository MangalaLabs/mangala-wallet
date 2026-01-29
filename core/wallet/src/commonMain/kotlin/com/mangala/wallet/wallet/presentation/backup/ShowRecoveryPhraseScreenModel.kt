package com.mangala.wallet.wallet.presentation.backup

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.wallet.usecases.GetAllWalletsUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ShowRecoveryPhraseUiState(
    val recoveryPhrase: List<String> = emptyList(),
    val isLoading: Boolean = false
)

class ShowRecoveryPhraseScreenModel(
    private val getAllWalletsUseCase: GetAllWalletsUseCase
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow(ShowRecoveryPhraseUiState())
    val uiState: StateFlow<ShowRecoveryPhraseUiState> = _uiState.asStateFlow()

    init {
        loadRecoveryPhrase()
    }

    private fun loadRecoveryPhrase() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Get the first wallet's recovery phrase
                val wallets = getAllWalletsUseCase()
                val wordsString = wallets.firstOrNull()?.words ?: ""
                val recoveryPhrase = if (wordsString.isNotEmpty()) {
                    wordsString.split(" ")
                } else {
                    emptyList()
                }

                _uiState.value = _uiState.value.copy(
                    recoveryPhrase = recoveryPhrase,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    recoveryPhrase = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    fun onContinueClick() {
        // Handle continue action - mark wallet as backed up
        screenModelScope.launch {
            try {
                val wallets = getAllWalletsUseCase()
                wallets.firstOrNull()?.let { wallet ->
                    // TODO: Update wallet to mark it as backed up
                    // updateWalletUseCase(wallet.copy(isBackedUp = true))
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
