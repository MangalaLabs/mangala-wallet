package com.mangala.wallet.features.send_base.step5

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.blockchain.usecases.GetBlockchainExplorerLinkUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Step5SendSuccessScreenModel(
    val txHash: String,
    val blockchainUid: String,
    getBlockchainExplorerLinkUseCase: GetBlockchainExplorerLinkUseCase
): BaseScreenModel(), KoinComponent {

    private val _uiModel = MutableStateFlow(Step5SendSuccessScreenUiModel("", ""))
    val uiModel get() = _uiModel.asStateFlow()

    init {
        screenModelScope.launch {
            val txLink = getBlockchainExplorerLinkUseCase.getTxLink(blockchainUid, txHash)
            _uiModel.update { Step5SendSuccessScreenUiModel(txHash, txLink) }
        }
    }
}