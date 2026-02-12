package com.mangala.wallet.wallet.presentation.restore

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.usecases.RestoreWalletUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ImportWalletState {
    data object Idle : ImportWalletState
    data object Restoring : ImportWalletState
    data object Success : ImportWalletState
    data class Error(val message: String) : ImportWalletState
}

class ImportWalletSuccessScreenModel(
    private val restoreWalletUseCase: RestoreWalletUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) : BaseScreenModel() {

    // Use independent scope on IO dispatcher so it doesn't block UI/navigation
    private val restoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _importState = MutableStateFlow<ImportWalletState>(ImportWalletState.Idle)
    val importState = _importState.asStateFlow()

    fun restoreWallet(mnemonicWords: List<String>, walletName: String) {
        _importState.value = ImportWalletState.Restoring
        restoreScope.launch {
            try {
                val blockchainType = getSelectedNetworkUseCase().blockchainType
                val result = restoreWalletUseCase(mnemonicWords, walletName, blockchainType)
                if (result.isSuccess) {
                    _importState.value = ImportWalletState.Success
                } else {
                    _importState.value = ImportWalletState.Error(
                        result.exceptionOrNull()?.message ?: "Failed to restore wallet"
                    )
                }
            } catch (e: Exception) {
                _importState.value = ImportWalletState.Error(
                    e.message ?: "Failed to restore wallet"
                )
            }
        }
    }

    fun dismissError() {
        _importState.value = ImportWalletState.Idle
    }
}
