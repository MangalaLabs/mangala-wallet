package com.mangala.wallet.features.send.presentation.sendsignedtransaction

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SendSignedTransactionUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.RawTransaction
import com.mangala.wallet.features.chains.evmcompatible.model.SignedTransactionResponse
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SendSignedTransactionScreenModel(
    private val signedTransactionResponse: SignedTransactionResponse,
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase
): BaseScreenModel() {
    
    private val _uiState: MutableStateFlow<String> = MutableStateFlow("")
    val uiState: StateFlow<String> = _uiState.asStateFlow()

    fun send() {
        screenModelScope.launch {
            val blockchainType = signedTransactionResponse.signTransactionRequest.blockchainType
            val chain = Chain.fromBlockchainType(blockchainType)
            val rpcUrl = blockchainType.getRpcUrl().first()
            val to = signedTransactionResponse.signTransactionRequest.transactionData.to ?: return@launch
            val txHash = sendSignedTransactionUseCase(
                rpcUrl = rpcUrl,
                chain = chain,
                from = Address(signedTransactionResponse.signTransactionRequest.fromAddress),
                rawTransaction = RawTransaction(
                    gasPrice = signedTransactionResponse.signTransactionRequest.gasPrice,
                    gasLimit = signedTransactionResponse.signTransactionRequest.gasLimit,
                    to = to,
                    value = signedTransactionResponse.signTransactionRequest.transactionData.value,
                    nonce = signedTransactionResponse.signTransactionRequest.nonce,
                    data = signedTransactionResponse.signTransactionRequest.transactionData.input
                ),
                signature = signedTransactionResponse.getSignature()
            )
            _uiState.update { txHash }
        }
    }
}