package com.mangala.wallet.features.chains.antelope.presentation.backupaccount.keycert

import androidx.compose.runtime.key
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPrivateKeyUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.keycert.CreateKeyCertUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BackupWithKeyCertScreenModel(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val createKeyCertUseCase: CreateKeyCertUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAccountPrivateKeyUseCase: GetAccountPrivateKeyUseCase
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<BackupWithKeyCertUiState> =
        MutableStateFlow(BackupWithKeyCertUiState.Loading)
    val uiState: StateFlow<BackupWithKeyCertUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            val blockchainType = getSelectedNetworkUseCase().blockchainType

            val accounts = getAccountsUseCase(blockchainType)
            val firstAccount = accounts.firstOrNull() ?: run {
                _uiState.value = BackupWithKeyCertUiState.Error("No account found")
                return@launch
            }
            val privateKey = getAccountPrivateKeyUseCase(firstAccount.accountName, "owner") ?: run {
                _uiState.value = BackupWithKeyCertUiState.Error("No private key found")
                return@launch
            }

            val keyCert =
                createKeyCertUseCase(privateKey, blockchainType, firstAccount.accountName, "owner")
            val certString = createKeyCertUseCase.toAnchorCertString(keyCert)

            _uiState.update {
                BackupWithKeyCertUiState.Success(
                    permissionName = "owner",
                    accountName = firstAccount.accountName,
                    keyCertString = certString,
                    encryptionWords = keyCert.encryptionWords?.joinToString(" ").orEmpty()
                )
            }
        }
    }
}