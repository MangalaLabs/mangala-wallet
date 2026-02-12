package com.mangala.wallet.wallet.presentation.restore

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.usecases.RestoreWalletUseCase
import com.mangala.wallet.pin.domain.PINManager
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.bip39.BIP39_WORDLIST_ENGLISH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ValidationError {
    data object InvalidLength : ValidationError
    data object InvalidWord : ValidationError
    data object InvalidChecksum : ValidationError
}

class RestoreRecoveryPhraseScreenModel(
    private val pinManager: PINManager,
    private val restoreWalletUseCase: RestoreWalletUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
) : BaseScreenModel() {

    private val _uiState : MutableStateFlow<RestoreRecoveryPhraseScreenUiState> = MutableStateFlow(RestoreRecoveryPhraseScreenUiState.NoImported)
    val uiState = _uiState.asStateFlow()

    // Track if navigation has been handled to prevent duplicate navigations
    private var hasNavigated = false

    private val _recoveryPhrase = MutableStateFlow("")
    val recoveryPhrase = _recoveryPhrase.asStateFlow()

    private val _recoveryPhraseState = MutableStateFlow(mutableListOf<Pair<String, Boolean>>())
    val recoveryPhraseState = _recoveryPhraseState.asStateFlow()

    private val _isRestoreButtonEnabled = MutableStateFlow(false)
    val isRestoreButtonEnabled = _isRestoreButtonEnabled.asStateFlow()

    private val _validationError = MutableStateFlow<ValidationError?>(null)
    val validationError = _validationError.asStateFlow()

    companion object {
        private val VALID_WORD_COUNTS = setOf(12, 15, 18, 21, 24)
    }

    fun onInputRecoveryPhrase(text: String){
        val wordList = text.split(" ")
        if (wordList.size > 24) return

        val correctList = mutableListOf<Pair<String, Boolean>>()
        wordList.forEach{ word ->
            val check = isCorrectWord(word)
            correctList.add(Pair(word, check))
        }

        _recoveryPhraseState.value = correctList
        _recoveryPhrase.value = text

        // Clear validation error when user is typing
        _validationError.value = null

        val validWordCount = correctList.count { it.second }
        if (validWordCount >= 12 && wordList.size in VALID_WORD_COUNTS) {
            val result = verifyWallet()
            _isRestoreButtonEnabled.value = result
        } else {
            _isRestoreButtonEnabled.value = false
            // Show word count hint when user has entered enough words but count is invalid
            if (validWordCount >= 12 && wordList.size !in VALID_WORD_COUNTS) {
                _validationError.value = ValidationError.InvalidLength
            }
        }
    }

    private fun isCorrectWord(text: String): Boolean {
        return text.lowercase() in BIP39_WORDLIST_ENGLISH
    }

    private fun verifyWallet(): Boolean {
        val list = recoveryPhrase.value.trim().split(" ")
        val result = restoreWalletUseCase.verifyWallet(list)
        if (result.isFailure) {
            _validationError.value = when (result.exceptionOrNull()) {
                is RestoreWalletUseCase.Error.InvalidLength -> ValidationError.InvalidLength
                is RestoreWalletUseCase.Error.InvalidWord -> ValidationError.InvalidWord
                is RestoreWalletUseCase.Error.InvalidChecksum -> ValidationError.InvalidChecksum
                else -> null
            }
        }
        return result.isSuccess
    }

    private var isImporting = false

    fun importWallet() {
        // Prevent multiple clicks
        if (isImporting) return
        isImporting = true
        hasNavigated = false

        screenModelScope.launch {
            try {
                val list = recoveryPhrase.value.split(" ")
                // Don't call restoreWalletUseCase here - ImportWalletSuccessScreen will handle it
                // This prevents duplicate API calls
                val blockchainTypeUid = getSelectedNetworkUseCase().blockchainType.uid
                _uiState.value =
                    RestoreRecoveryPhraseScreenUiState.Imported(list, blockchainTypeUid, "")
                onInputRecoveryPhrase("")
            } finally {
                isImporting = false
            }
        }
    }

    fun resetUiState() {
        _uiState.value = RestoreRecoveryPhraseScreenUiState.NoImported
        // Note: hasNavigated is intentionally NOT reset here.
        // It's only reset when a new import is initiated via importWallet().
    }

    fun isPinExist(): Boolean {
        return pinManager.isPINSetup()
    }

    /**
     * Check and mark navigation as handled.
     * Returns true if navigation should proceed, false if already handled.
     */
    fun shouldNavigate(): Boolean {
        if (hasNavigated) return false
        hasNavigated = true
        return true
    }
}
