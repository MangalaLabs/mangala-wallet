package com.mangala.wallet.features.settings.network

import cafe.adriel.voyager.core.model.ScreenModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent

class NetworkBottomSheetScreenModel(
    private val selectedNetwork: BlockchainNetworkData?,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
): BaseScreenModel() {

    private val _uiModel = MutableStateFlow(NetworkScreenModelUiModel())
    val uiModel: StateFlow<NetworkScreenModelUiModel> get() = _uiModel

    init {
        buildList()
    }

    private fun buildList() {
        val isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()

        BlockchainNetworkData.getAllBlockchainNetworkSupported(isDevelopmentEnvironment).map {
            val isSelected = it.name == selectedNetwork?.name
            NetworkScreenModelItemUiModel(it, isSelected)
        }.let { items ->
            _uiModel.value = NetworkScreenModelUiModel(query = _uiModel.value.query, 1L, items)
        }
    }

    fun onChangeQuery(query: String) {
        _uiModel.update { it.copy(query = query) }
    }
}
