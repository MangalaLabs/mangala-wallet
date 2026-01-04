package com.mangala.browser_bridge_base.switchchain

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.SaveSelectedNetworkUseCase
import com.mangala.wallet.features.chains.BlockSyncer
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.launch

class SwitchChainScreenModel(
    private val saveSelectedNetworkUseCase: SaveSelectedNetworkUseCase,
    private val evmBlockSyncer: BlockSyncer,
    buildEnvironmentProvider: BuildEnvironmentProvider
): BaseScreenModel() {

    val isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()

    fun saveNetwork(chainId: Long) {
        screenModelScope.launch {
            BlockchainNetworkData.getAllBlockchainNetworkSupported(isDevelopmentEnvironment).find { it.chainId == chainId }
                ?.let { item ->
                    saveSelectedNetworkUseCase(item)
                    evmBlockSyncer.stopSync()
                }
        }
    }
}