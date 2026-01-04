package com.mangala.wallet.features.chains.antelope.presentation.backupaccount.backupPrivateKey

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPrivateKeyUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ListAccountPublicKeysUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.ClipboardFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BackupAntelopePrivateKeyScreenModel(
    private val getAccountPrivateKeyUseCase: GetAccountPrivateKeyUseCase,
    private val listAccountPublicKeysUseCase: ListAccountPublicKeysUseCase,
    private val clipboardFactory: ClipboardFactory,
    private val accountName: String,
    private val permissionName: String
) : BaseScreenModel() {

    private val _accountPublicKeys = MutableStateFlow<List<String>>(emptyList())
    val accountPublicKeys = _accountPublicKeys.asStateFlow()

    private val _selectedPublicKey = MutableStateFlow("")
    val selectedPublicKey = _selectedPublicKey.asStateFlow()

    private val _selectedPrivateKey = MutableStateFlow("")
    val selectedPrivateKey = _selectedPrivateKey.asStateFlow()

    private val _isShowPrivateKey = MutableStateFlow(false)
    val isShowPrivateKey = _isShowPrivateKey.asStateFlow()

    init {
        getAccountPublicKeys()
    }

    private fun getAccountPublicKeys(){
        screenModelScope.launch {
            val accountPublicKeys = listAccountPublicKeysUseCase(accountName, permissionName).map { it.key }
            _accountPublicKeys.value = accountPublicKeys
            accountPublicKeys.firstOrNull()?.let { onSelectPublicKey(it) }
        }
    }

    fun onSelectPublicKey(publicKey: String){
        _selectedPublicKey.value = publicKey
        screenModelScope.launch {
            _selectedPrivateKey.value = getAccountPrivateKeyUseCase.getAccountPrivateKeyByPublicKey(publicKey)?.toString() ?: ""
        }
    }

    fun onToggleShowHidePrivateKey(){
        _isShowPrivateKey.value = !isShowPrivateKey.value
    }

    fun copyPrivateKeyToClipboard(){
        // TODO hard code
        clipboardFactory.copyText("Mangala Copy", selectedPrivateKey.value)
    }
}