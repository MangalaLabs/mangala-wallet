package com.mangala.wallet.wallet.presentation.restore

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.usecases.RestoreWalletUseCase
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperConstants
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.bip39.BIP39_WORDLIST_ENGLISH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RestoreRecoveryPhraseScreenModel(
    private val secureStorageWrapper: SecureStorageWrapper,
    private val restoreWalletUseCase: RestoreWalletUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
) : BaseScreenModel() {

    private val _uiState : MutableStateFlow<RestoreRecoveryPhraseScreenUiState> = MutableStateFlow(RestoreRecoveryPhraseScreenUiState.NoImported)
    val uiState = _uiState.asStateFlow()

    private val _recoveryPhrase = MutableStateFlow("")
    val recoveryPhrase = _recoveryPhrase.asStateFlow()

    private val _recoveryPhraseState = MutableStateFlow(mutableListOf<Pair<String, Boolean>>())
    val recoveryPhraseState = _recoveryPhraseState.asStateFlow()

    private val _isRestoreButtonEnabled = MutableStateFlow(false)
    val isRestoreButtonEnabled = _isRestoreButtonEnabled.asStateFlow()

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

        if (correctList.count { it.second } >= 12) _isRestoreButtonEnabled.value = verifyWallet()
        else _isRestoreButtonEnabled.value = false
    }

    private fun isCorrectWord(text: String): Boolean {
        return text.lowercase() in BIP39_WORDLIST_ENGLISH
    }

    private fun verifyWallet(): Boolean {
        val list = recoveryPhrase.value.trim().split(" ")
        return restoreWalletUseCase.verifyWallet(list).isSuccess
    }

    fun importWallet() {
        screenModelScope.launch {
            val list = recoveryPhrase.value.split(" ")
            if (isPinExist()) {
                restoreWalletUseCase(list, "Main")
            }
            val blockchainTypeUid = getSelectedNetworkUseCase().blockchainType.uid
            _uiState.value =
                RestoreRecoveryPhraseScreenUiState.Imported(list, blockchainTypeUid, "")
            onInputRecoveryPhrase("")
        }
    }

    fun resetUiState() {
        _uiState.value = RestoreRecoveryPhraseScreenUiState.NoImported
    }

    private fun getPin(): String {
        return secureStorageWrapper.getValue(SecureStorageWrapperConstants.PIN_KEY) ?: ""
    }

    fun isPinExist(): Boolean {
        return getPin().isNotEmpty()
    }
}