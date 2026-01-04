package com.mangala.wallet.features.send.presentation.step4

import cafe.adriel.voyager.core.model.screenModelScope
import com.benasher44.uuid.uuid4
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.evmcompatible.core.amountToBigInt
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EstimateGasUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetLatestBlockUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetNonceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetRecommendedGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionFeeOptionsUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SendSignedTransactionUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SendTokenUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.RawTransaction
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionType
import com.mangala.wallet.features.chains.evmcompatible.model.SignedTransactionResponse
import com.mangala.wallet.features.contacts.domain.usecases.GetContactByIdUseCase
import com.mangala.wallet.features.send_base.step4.evm.BaseEvmStep4VerifyAndSendScreenModel
import com.mangala.wallet.features.send_base.step4.evm.BaseEvmStep4VerifyAndSendScreenUiState
import com.mangala.wallet.model.blockchain.Chain
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EvmStep4VerifyAndSendScreenModel(
    contactId: Long?,
    blockchainUid: String,
    tokenId: Long,
    recipientAddress: String,
    amount: String,
    accountId: String,
    getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getAccountByIdUseCase: GetAccountByIdUseCase,
    fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    getSelectedCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getTransactionFeeOptionsUseCase: GetTransactionFeeOptionsUseCase,
    getContactUseCase: GetContactByIdUseCase,
    getTokenBalanceByTokenIdUseCase: GetTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase: GetRecommendedGasPriceUseCase,
    sendTokenUseCase: SendTokenUseCase,
    estimateGasUseCase: EstimateGasUseCase,
    getNativeCoinUseCase: GetNativeCoinUseCase,
    getLatestBlockUseCase: GetLatestBlockUseCase,
    getSelectedWalletUseCase: GetSelectedWalletUseCase,
    getNonceUseCase: GetNonceUseCase,
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase
): BaseEvmStep4VerifyAndSendScreenModel(contactId, blockchainUid, tokenId, recipientAddress, amount, accountId, getCurrentCurrencyCodeUseCase, getAccountByIdUseCase, fetchTokenPriceUseCase, getSelectedCurrencyCodeUseCase, getTransactionFeeOptionsUseCase, getContactUseCase, getTokenBalanceByTokenIdUseCase, getRecommendedGasPriceUseCase, sendTokenUseCase, estimateGasUseCase, getNativeCoinUseCase, getLatestBlockUseCase, getSelectedWalletUseCase, getNonceUseCase) {

    private lateinit var signedTransactionResponse: SignedTransactionResponse
    private lateinit var signTransactionRequestId: String

    fun getSignTransactionRequest(): SignTransactionRequest {
        val uiData = _uiState.value as BaseEvmStep4VerifyAndSendScreenUiState.Data
        val gasPrice = uiData.gasPrice
        val gasLimit = uiData.estimatedGasLimit
        val selectedWallet = getSelectedWalletUseCase()

        signTransactionRequestId = uuid4().toString()

        return SignTransactionRequest(
            requestId = signTransactionRequestId,
            walletId = selectedWallet?.id.orEmpty(),
            accountId = accountId,
            nonce = nonce ?: 0L,
            blockchainType = blockchainType,
            transactionData = if (token.isCoin) {
                buildSendTransactionData(
                    Address(recipientAddress),
                    amount.amountToBigInt()
                )
            } else {
                buildTransferTransactionData(
                    Address(token.contractAddress),
                    Address(recipientAddress),
                    amount.amountToBigInt()
                )
            },
            gasPrice = gasPrice!!, // TODO: Null check
            gasLimit = gasLimit!!, // TODO: Null check
            gasFiatValue = uiData.selectedTransactionFee?.transactionFeeFiatValueString.orEmpty(),
            transactionType = if (token.isCoin) {
                SignTransactionType.SendCoinOrErc20Token(
                    amount = "", // TODO: #325 Fill in data
                    symbol = "",
                    fiatValue = ""
                )
            } else {
                SignTransactionType.SendCoinOrErc20Token(
                    amount = "", // TODO: #325 Fill in data
                    symbol = "",
                    fiatValue = ""
                )
            },
            fromAddress = currentAccount.bip44Address,
            contactName = uiData.contact?.name,
            contactAddress = uiData.contact?.address
        )
    }

    fun onScannedSignedTransaction(signedTransaction: SignedTransactionResponse) {
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
                if (it is BaseEvmStep4VerifyAndSendScreenUiState.Data) {
                    it.copy(
                        txHash = txHash
                    )
                } else it
            }
        }
    }
}