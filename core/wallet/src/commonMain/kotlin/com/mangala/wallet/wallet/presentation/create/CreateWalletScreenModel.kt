package com.mangala.wallet.wallet.presentation.create

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.usecases.CreateWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.RestoreWalletUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.UpdateAccountStatusUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class CreateWalletScreenModel(
    private val createWalletUseCase: CreateWalletUseCase,
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase,
    private val restoreWalletUseCase: RestoreWalletUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
): BaseScreenModel() {

    val onCreateDone: Channel<Unit> = Channel()

    fun createWallet(blockchainUid: String, antelopeAccountName: String?) {
        screenModelScope.launch {
            val blockchainType = BlockchainType.fromUid(blockchainUid)

            when(blockchainType.networkType) {

                NetworkType.ANTELOPE -> {
                    antelopeAccountName?.let {
                        updateAccountStatusUseCase(
                            it,
                            isTemp = false,
                            blockchainType,
                            createAccountState = AntelopeAccount.CreateAccountState.DONE
                        )
                    }
                }
                NetworkType.EVM -> {
                    createWalletUseCase(wordsCount = 12, passphrase = "", blockchainType)
                }
                NetworkType.BITCOIN -> {
                    createWalletUseCase(wordsCount = 12, passphrase = "", blockchainType)
                }
                NetworkType.OTHER -> TODO()
                NetworkType.UNSUPPORTED -> TODO()
            }
        }
    }

    fun restoreWallet(listString: List<String>, name: String) {
        screenModelScope.launch {
            restoreWalletUseCase(listString, name, getSelectedNetworkUseCase().blockchainType)
            onCreateDone.trySend(Unit)
        }
    }
}