package com.mangala.wallet.features.wallet.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WalletTabScreenModel : ScreenModel, KoinComponent {

    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase by inject()

    private val _selectedNetwork = MutableStateFlow<BlockchainNetworkData?>(null)
    val selectedNetwork: StateFlow<BlockchainNetworkData?> = _selectedNetwork.asStateFlow()

    init {
        screenModelScope.launch {
            getSelectedNetworkUseCase.invokeFlow().collectLatest { network ->
                _selectedNetwork.value = network
            }
        }
    }
}
