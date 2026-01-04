package com.mangala.wallet.features.nft.presentation.send.confirmation

import cafe.adriel.voyager.core.model.screenModelScope
import com.benasher44.uuid.uuid4
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.evmcompatible.core.amountToBigInt
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetLatestBlockUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetNonceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetRecommendedGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionFeeOptionsUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SendSignedTransactionUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.RawTransaction
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionType
import com.mangala.wallet.features.chains.evmcompatible.model.SignedTransactionResponse
import com.mangala.wallet.features.contacts.domain.usecases.GetContactByIdUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.GetNftByTokenIdUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.SendNftUseCase
import com.mangala.wallet.features.nft_base.presentation.send.confirmation.BaseSendNftConfirmationScreenModel
import com.mangala.wallet.features.nft_base.presentation.send.confirmation.SendNftConfirmationScreenUiState
import com.mangala.wallet.model.blockchain.Chain
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SendNftConfirmationScreenModel(
    blockchainUid: String,
    accountId: String,
    toAddress: String, // use this only to calculate recipientAddress
    collectionContractAddress: String,
    tokenId: String,
    contactId: Long?,
    sendNftUseCase: SendNftUseCase,
    getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    getAccountByIdUseCase: GetAccountByIdUseCase,
    getNftByTokenIdUseCase: GetNftByTokenIdUseCase,
    getContactByIdUseCase: GetContactByIdUseCase,
    getRecommendedGasPriceUseCase: GetRecommendedGasPriceUseCase,
    getTransactionFeeOptionsUseCase: GetTransactionFeeOptionsUseCase,
    fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    getNativeCoinUseCase: GetNativeCoinUseCase,
    getTokenBalanceByTokenIdUseCase: GetTokenBalanceByTokenIdUseCase,
    getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getLatestBlockUseCase: GetLatestBlockUseCase,
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    getNonceUseCase: GetNonceUseCase,
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase
): BaseSendNftConfirmationScreenModel(
    blockchainUid, accountId, toAddress, collectionContractAddress, tokenId, contactId, sendNftUseCase, getSelectedNetworkUseCase, getAccountByIdUseCase, getNftByTokenIdUseCase, getContactByIdUseCase, getRecommendedGasPriceUseCase, getTransactionFeeOptionsUseCase, fetchTokenPriceUseCase, getNativeCoinUseCase, getTokenBalanceByTokenIdUseCase, getCurrentCurrencyCodeUseCase, getLatestBlockUseCase, getNonceUseCase
) {
    private lateinit var signedTransactionResponse: SignedTransactionResponse
    private lateinit var signTransactionRequestId: String

    fun getSignTransactionRequest(): SignTransactionRequest? {
        val uiData = _uiState.value as SendNftConfirmationScreenUiState.Data
        val gasPrice = uiData.gasPrice
        val gasLimit = uiData.estimatedGasLimit
        val selectedWallet = getSelectedWalletUseCase()
        val fromAddress = currentAccount.bip44Address

        val requestId = uuid4().toString()
        println("requestId: $requestId")

        return SignTransactionRequest(
            requestId = requestId,
            walletId = selectedWallet?.id.orEmpty(),
            accountId = accountId,
            nonce = nonce ?: 0L,
            blockchainType = blockchainType,
            transactionData = sendNftUseCase.buildTransactionData(uiData.nft, fromAddress, recipientAddress) ?: return null,
            gasPrice = gasPrice!!, // TODO: Null check
            gasLimit = gasLimit!!, // TODO: Null check
            gasFiatValue = uiData.selectedTransactionFee?.transactionFeeFiatValueString.orEmpty(),
            transactionType = SignTransactionType.SendErc721Or1155Token(
                collectionName = "", // TODO: #327 fill in data
                tokenId = ""
            ),
            fromAddress = currentAccount.bip44Address,
            contactName = uiData.contact?.name,
            contactAddress = uiData.contact?.address
        )
    }

    fun onScannedSignedTransaction(signedTransaction: SignedTransactionResponse) {
        println("requestId scanned: ${signedTransaction.signTransactionRequest.requestId}")
        signedTransactionResponse = signedTransaction
    }

    fun sendSignedTransaction() {
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
            _uiState.update {
                if (it is SendNftConfirmationScreenUiState.Data) {
                    it.copy(
                        txHash = txHash
                    )
                } else it
            }
        }
    }
}