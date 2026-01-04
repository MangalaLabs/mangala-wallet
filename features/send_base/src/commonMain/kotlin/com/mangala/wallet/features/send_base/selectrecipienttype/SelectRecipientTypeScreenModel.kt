package com.mangala.wallet.features.send_base.selectrecipienttype

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectRecipientTypeScreenModel(
    private val accountId: String,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<SelectRecipientTypeScreenUiState> =
        MutableStateFlow(SelectRecipientTypeScreenUiState.Loading)
    val uiState get() = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            val network = getSelectedNetworkUseCase()

            _uiState.update {
                SelectRecipientTypeScreenUiState.Data(
                    blockchainUid = network.blockChainUid
                )
            }
        }
    }
}