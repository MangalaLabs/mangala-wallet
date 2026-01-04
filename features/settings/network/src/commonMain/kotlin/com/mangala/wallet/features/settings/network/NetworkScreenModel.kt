package com.mangala.wallet.features.settings.network

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveSelectedNetworkUseCase
import com.mangala.wallet.features.chains.BlockSyncer
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NetworkScreenModel(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val saveSelectedNetworkUseCase: SaveSelectedNetworkUseCase,
    private val blockSyncer: BlockSyncer,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
): BaseScreenModel() {

    private val _uiModel = MutableStateFlow(NetworkScreenModelUiModel())
    val uiModel: StateFlow<NetworkScreenModelUiModel> get() = _uiModel

    init {
        screenModelScope.launch {
            collectSelectedNetwork()
        }
    }

    override fun doOnComposableStarted() = Unit

    fun onChangeQuery(query: String) {
        _uiModel.update { it.copy(query = query) }
    }

    fun onClickSelectNetwork(item: NetworkScreenModelItemUiModel) {
        screenModelScope.launch {
            if (item.isSelected) return@launch // Prevent unnecessary saves

            _uiModel.update { it.copy(chainIdSelected = item.network.chainId) }
            saveSelectedNetworkUseCase(item.network)
            blockSyncer.stopSync()
        }
    }

    private suspend fun collectSelectedNetwork() {
        val isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()

        getSelectedNetworkUseCase.invokeFlow().collect { selectedNetwork ->
            BlockchainNetworkData.getAllBlockchainNetworkSupported(buildEnvironmentProvider.isDevelopmentEnvironment()).map {
                val isSelected = it.name == selectedNetwork.name
                NetworkScreenModelItemUiModel(it, isSelected)
            }.let { items ->
                _uiModel.value = NetworkScreenModelUiModel(query = _uiModel.value.query, selectedNetwork.chainId, items)
            }
        }
    }
}
