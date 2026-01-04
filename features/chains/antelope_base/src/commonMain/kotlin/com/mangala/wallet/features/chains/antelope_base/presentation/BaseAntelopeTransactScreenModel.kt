package com.mangala.wallet.features.chains.antelope_base.presentation

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.domain.model.InvalidRequestData
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.antelope.base.domain.model.Transaction
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseTransactUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface AntelopeTransactionHandler {
    var blockchainUid: String
    val coroutineScope: CoroutineScope
    val transactUseCase: BaseTransactUseCase

    var resourceProviderResponse: ResourceProviderResponse?

    val blockchainType get() = BlockchainType.fromUid(blockchainUid)

    fun onRequestTransaction() {
        showLoadingState()

        coroutineScope.launch {
            val requestTransactionResponse = requestTransaction()

            requestTransactionResponse.fold(
                onSuccess = {
                    if (it is ResourceProviderResponse.InvalidRequest && it.invalidRequestData is InvalidRequestData.ResourceProviderError) {
                        fallbackPushTransactionWithoutResourceProvider()
                        return@fold
                    }
                    println("=== Pre on request transaction success ===")
                    onRequestTransactionSuccess(it)
                },
                onFailure = {
                    fallbackPushTransactionWithoutResourceProvider()
                }
            )
        }
    }

    private suspend fun fallbackPushTransactionWithoutResourceProvider() {
        pushTransactionWithoutResourceProvider().fold(
            onSuccess = {
                onPushTransactionSuccess(it)
            },
            onFailure = {
                onPushTransactionFail(it)
            }
        )
    }

    private fun onRequestTransactionSuccess(
        resourceProviderResponse: ResourceProviderResponse
    ) {
        this.resourceProviderResponse = resourceProviderResponse

        when (resourceProviderResponse) {
            is ResourceProviderResponse.FeeRequired -> {
                onRequestTransactionFeeRequired(resourceProviderResponse)
            }

            is ResourceProviderResponse.InvalidRequest -> {
                onRequestTransactionInvalidRequest()
            }

            ResourceProviderResponse.ResourceNotRequired, is ResourceProviderResponse.ResourcePaidForFree -> {
                onRequestTransactionResourceCovered()
            }
        }
    }
    fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired)
    fun onRequestTransactionInvalidRequest()
    fun onRequestTransactionResourceCovered()
    suspend fun requestTransaction(): Result<ResourceProviderResponse>
    fun onDismissTransactionFeeBreakdown()
    fun onPinPromptShown()
    fun onConfirmResourceProviderFee()
    fun onAuthenticationSuccess(senderAccountName: String) {
        coroutineScope.launch {
            showLoadingState()

            when (val response = resourceProviderResponse) {
                is ResourceProviderResponse.FeeRequired -> {
                    onDismissTransactionFeeBreakdown()

                    val resourceProviderResponse = resourceProviderResponse as? ResourceProviderResponse.FeeRequired ?: return@launch

                    pushResourceProvidedTransaction(resourceProviderResponse.newTransaction, senderAccountName)
                }
                is ResourceProviderResponse.ResourcePaidForFree -> {
                    pushResourceProvidedTransaction(response.newTransaction, senderAccountName)
                }
                ResourceProviderResponse.ResourceNotRequired -> {
                    val result = pushTransactionWithoutResourceProvider()

                    result.fold(
                        onSuccess = {
                            onPushTransactionSuccess(it)
                        },
                        onFailure = {
                            onPushTransactionFail(it)
                        }
                    )
                }
                else -> {}
            }
        }
    }
    private suspend fun pushResourceProvidedTransaction(newTransaction: Transaction, senderAccountName: String) {
        val response = transactUseCase.pushResourceProvidedTransaction(
            blockchainType = blockchainType,
            senderAccountName = senderAccountName,
            transaction = newTransaction
        )

        response.fold(
            onSuccess = {
                onPushTransactionSuccess(it)
            },
            onFailure = {
                onPushTransactionFail(it)
            }
        )
    }
    suspend fun pushTransactionWithoutResourceProvider(): Result<String>
    fun showLoadingState()
    fun onPushTransactionSuccess(txHash: String)
    fun onPushTransactionFail(throwable: Throwable)
}

abstract class BaseAntelopeTransactScreenModel(
    override val transactUseCase: BaseTransactUseCase,
    override var blockchainUid: String,
): BaseScreenModel(), AntelopeTransactionHandler {
    override val coroutineScope = screenModelScope
    override var resourceProviderResponse: ResourceProviderResponse? = null
}