package com.mangala.wallet.features.addressbook.presentation.contact.recent

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.blockchain.usecases.GetBlockchainExplorerLinkUseCase
import com.mangala.wallet.features.addressbook.data.model.TransactionDetailModel
import com.mangala.wallet.features.addressbook.domain.repository.transaction.TransactionRepository
import com.mangala.wallet.features.addressbook.presentation.contact.recent.model.RecentTransactionDetailUiState
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.currentTimeInMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionDetailScreenModel(
    transactionId: String,
    private val repository: TransactionRepository,
    getBlockchainExplorerLinkUseCase: GetBlockchainExplorerLinkUseCase
): BaseScreenModel() {
    private val _uiState = MutableStateFlow<RecentTransactionDetailUiState>(RecentTransactionDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        screenModelScope.launch(Dispatchers.IO) {
            val startTime = currentTimeInMillis()
            
            val result = repository.getTransactionDetailById(transactionId)?.let { transaction ->
                val txBlockExplorerLink = getBlockchainExplorerLinkUseCase.getTxLink(
                    blockchainUid = transaction.blockchainType.id,
                    txHash = transaction.transaction.transactionHash,
                )
                RecentTransactionDetailUiState.Success(
                    transactionDetail = transaction,
                    txBlockExplorerLink = txBlockExplorerLink
                )
            } ?: RecentTransactionDetailUiState.Error(Exception("Transaction not found"))
            
            val elapsedTime = currentTimeInMillis() - startTime
            val remainingDelay = MIN_LOADING_DURATION - elapsedTime
            
            if (remainingDelay > 0) {
                delay(remainingDelay)
            }
            
            _uiState.update { result }
        }
    }

    companion object {
        private const val MIN_LOADING_DURATION = 500L // Minimum loading duration in milliseconds
    }
}