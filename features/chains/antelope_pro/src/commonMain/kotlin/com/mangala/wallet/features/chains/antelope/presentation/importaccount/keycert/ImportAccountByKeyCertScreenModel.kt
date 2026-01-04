package com.mangala.wallet.features.chains.antelope.presentation.importaccount.keycert

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope.domain.usecase.account.ImportAccountFromKeyCertUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.keycert.DecryptKeyCertUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.GenerateKeyAndUpdateAccountPermissionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.pin.domain.GetIsPinSetupUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.memtrip.eos.chain.actions.keycert.KeyCertArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImportAccountByKeyCertScreenModel(
    private val keyCert: String,
    private val decryptKeyCertUseCase: DecryptKeyCertUseCase,
    private val importAccountFromKeyCertUseCase: ImportAccountFromKeyCertUseCase,
    private val generateKeyAndUpdateAccountPermissionUseCase: GenerateKeyAndUpdateAccountPermissionUseCase,
    private val getIsPinSetupUseCase: GetIsPinSetupUseCase
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<ImportAccountByKeyCertUiState> =
        MutableStateFlow(ImportAccountByKeyCertUiState.Loading)
    val uiState: StateFlow<ImportAccountByKeyCertUiState> = _uiState.asStateFlow()

    private val _mnemonic: MutableState<String> = mutableStateOf("")
    val mnemonic: MutableState<String> = _mnemonic

    private lateinit var keyCertArgs: KeyCertArgs

    init {
        _uiState.update {
            keyCertArgs = decryptKeyCertUseCase(keyCert)

            ImportAccountByKeyCertUiState.Success(
                accountName = keyCertArgs.permissionLevel.actor,
                permissionName = keyCertArgs.permissionLevel.permission,
                blockchainType = BlockchainType.fromChainId(keyCertArgs.chainId)
            )
        }
    }

    fun onMnemonicChanged(mnemonic: String) {
        if (mnemonic.any { !it.isLetter() && !it.isWhitespace() }) {
            return
        }

        _mnemonic.value = mnemonic
    }

    fun onClickImport() {
        screenModelScope.launch {
            if (_uiState.value !is ImportAccountByKeyCertUiState.Success) return@launch

            _uiState.update {
                (it as? ImportAccountByKeyCertUiState.Success)?.copy(isLoading = true) ?: it
            }
            importAccountFromKeyCertUseCase(keyCertArgs, mnemonic.value.trim().split(" ")).fold(
                onSuccess = {
                    _uiState.update { ImportAccountByKeyCertUiState.CreateSuccess() }
                },
                onFailure = { throwable ->
                    _uiState.update { ImportAccountByKeyCertUiState.Error(throwable.message.toString()) }
                }
            )
        }
    }

    fun onCreateNewActiveKey() {
        screenModelScope.launch {
            _uiState.update {
                (it as? ImportAccountByKeyCertUiState.CreateSuccess)?.copy(isLoading = true) ?: it
            }
            generateKeyAndUpdateAccountPermissionUseCase(
                accountName = keyCertArgs.permissionLevel.actor,
                blockchainType = BlockchainType.fromChainId(keyCertArgs.chainId)
            ).fold(
                onSuccess = {
                    _uiState.update { ImportAccountByKeyCertUiState.CreatePermissionSuccess }
                },
                onFailure = { throwable ->
                    _uiState.update { ImportAccountByKeyCertUiState.Error(throwable.message.toString()) }
                }
            )
        }
    }

    fun onGetIsPinSetup(): Boolean {
        return getIsPinSetupUseCase()
    }

    fun getAccountName() = keyCertArgs.permissionLevel.actor
    fun getBlockchainUid() = BlockchainType.fromChainId(keyCertArgs.chainId).uid
}