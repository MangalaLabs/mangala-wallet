package com.mangala.wallet.features.transactionhistory.presentation.bitcoin.info

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.blockchain.usecases.GetBlockchainExplorerLinkUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.features.chains.bitcoin.domain.model.transaction.BitcoinTransaction
import com.mangala.wallet.features.chains.bitcoin.domain.repository.transaction.BitcoinTransactionRepository
import com.mangala.wallet.features.chains.bitcoin.domain.utils.formatBitcoin
import com.mangala.wallet.features.transactionhistory.presentation.utils.getTransactionAmount
import com.mangala.wallet.features.transactionhistory.presentation.utils.mapBitcoinTransactionType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.ClipboardFactory
import com.mangala.wallet.utils.FormatStyle
import com.mangala.wallet.utils.ShareFactory
import com.mangala.wallet.utils.formatDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransactionInfoBitcoinScreenModel(
    private val bitcoinAddress: String,
    private val txHash: String,
    private val bitcoinTransactionRepository: BitcoinTransactionRepository,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getBlockchainExplorerLinkUseCase: GetBlockchainExplorerLinkUseCase,
    private val clipboardFactory: ClipboardFactory,
    private val shareFactory: ShareFactory
) : BaseScreenModel() {

    private val _transactionDetails = MutableStateFlow<BitcoinTransactionDetailsUi?>(null)
    val transactionDetails: StateFlow<BitcoinTransactionDetailsUi?> = _transactionDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    fun loadTransactionDetails(forceRefresh: Boolean = false) {
        screenModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = ""

                val network = getSelectedNetworkUseCase()
                
                bitcoinTransactionRepository.getTransaction(
                    txId = txHash,
                    blockchainType = network.blockchainType,
                    forceRefresh = forceRefresh
                ).collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _isLoading.value = true
                            resource.data?.let { transaction ->
                                processTransactionData(transaction)
                            }
                        }
                        is Resource.Success -> {
                            _isLoading.value = false
                            resource.data?.let { transaction ->
                                processTransactionData(transaction)
                            } ?: run {
                                _error.value = "Transaction not found"
                            }
                        }
                        is Resource.Error -> {
                            _isLoading.value = false
                            resource.data?.let { transaction ->
                                processTransactionData(transaction)
                                if (_transactionDetails.value == null) {
                                    _error.value = resource.exception.message ?: "Unknown error"
                                }
                            } ?: run {
                                _error.value = resource.exception.message ?: "Unknown error"
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "An error occurred"
            }
        }
    }
    
    private fun processTransactionData(transaction: BitcoinTransaction) {
        val type = transaction.mapBitcoinTransactionType(bitcoinAddress)
        
        val amount = calculateAmount(transaction)
        
        val formattedDate = formatTimestamp(transaction.status.block_time)
        
        _transactionDetails.value = BitcoinTransactionDetailsUi(
            txid = transaction.txid,
            type = type,
            amount = amount,
            fee = transaction.fee,
            confirmed = transaction.status.confirmed,
            blockHeight = transaction.status.block_height ?: 0,
            blockHash = transaction.status.block_hash,
            formattedDate = formattedDate
        )
    }
    
    private fun calculateAmount(
        transaction: BitcoinTransaction
    ): String {
        val type = transaction.mapBitcoinTransactionType(bitcoinAddress)

        val prefix = if (type == TransactionType.RECEIVE) "+" else "-"
        val finalAmount = transaction.getTransactionAmount(type, bitcoinAddress)
        return "$prefix${(finalAmount).formatBitcoin()}"
    }

    fun copyToClipboard(text: String) {
        clipboardFactory.copyText("Mangala wallet", text)
    }

    private val _explorerUrl = MutableStateFlow("")
    val explorerUrl: StateFlow<String> = _explorerUrl.asStateFlow()

    fun loadExplorerUrl() {
        screenModelScope.launch {
            try {
                val network = getSelectedNetworkUseCase()
                val url = getBlockchainExplorerLinkUseCase.getTxLink(
                    blockchainUid = network.blockchainType.uid,
                    txHash = txHash,
                )
                
                if (url.isNotEmpty()) {
                    _explorerUrl.value = url
                }
            } catch (e: Exception) {
                _error.value = "Could not get explorer URL: ${e.message}"
            }
        }
    }
    
    private fun formatTimestamp(timestamp: Long?): String? {
        if (timestamp == null) return null
        
        val instant = Instant.fromEpochSeconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        
        return dateTime.formatDateTime(
            timeZone = TimeZone.currentSystemDefault(),
            dateStyle = FormatStyle.MEDIUM,
            timeStyle = FormatStyle.MEDIUM
        )
    }
    
    fun shareTransaction() {
        screenModelScope.launch {
            try {
                val network = getSelectedNetworkUseCase()
                val explorerUrl = getBlockchainExplorerLinkUseCase.getTxLink(
                    blockchainUid = network.blockchainType.uid,
                    txHash = txHash
                )

                shareFactory.shareText("Mangala share via ", explorerUrl)
            } catch (e: Exception) {
                _error.value = "Could not share transaction: ${e.message}"
            }
        }
    }
    
    fun refreshTransaction() {
        // Force refresh from network
        loadTransactionDetails(forceRefresh = true)
    }
}

data class BitcoinTransactionDetailsUi(
    val txid: String,
    val type: TransactionType, // "sent", "received", "unknown"
    val amount: String,
    val fee: Long,
    val confirmed: Boolean,
    val blockHeight: Int,
    val blockHash: String?,
    val formattedDate: String?
)