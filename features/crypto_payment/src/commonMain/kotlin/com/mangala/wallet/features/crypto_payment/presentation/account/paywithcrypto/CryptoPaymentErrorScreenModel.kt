package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.SaveSelectedNetworkUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.launch

class CryptoPaymentErrorScreenModel(
    private val saveSelectedNetworkUseCase: SaveSelectedNetworkUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
): BaseScreenModel() {
    fun changeNetwork(blockchainUid: String) {
        val isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()

        screenModelScope.launch {
            val blockchainNetworkData = BlockchainNetworkData.getBlockchainByUid(blockchainUid, isDevelopmentEnvironment)
            blockchainNetworkData?.let {
                saveSelectedNetworkUseCase(it)
            }
        }
    }
}