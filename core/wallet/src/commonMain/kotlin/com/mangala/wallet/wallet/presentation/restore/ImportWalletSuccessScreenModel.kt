package com.mangala.wallet.wallet.presentation.restore

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.usecases.RestoreWalletUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ImportWalletSuccessScreenModel(
    private val restoreWalletUseCase: RestoreWalletUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) : BaseScreenModel() {

    // Use independent scope on IO dispatcher so it doesn't block UI/navigation
    private val restoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun restoreWallet(mnemonicWords: List<String>, walletName: String) {
        restoreScope.launch {
            try {
                val blockchainType = getSelectedNetworkUseCase().blockchainType
                restoreWalletUseCase(mnemonicWords, walletName, blockchainType)
            } catch (e: Exception) {
                // Log the error - wallet is saved locally, API sync can happen later
                e.printStackTrace()
            }
        }
    }
}
