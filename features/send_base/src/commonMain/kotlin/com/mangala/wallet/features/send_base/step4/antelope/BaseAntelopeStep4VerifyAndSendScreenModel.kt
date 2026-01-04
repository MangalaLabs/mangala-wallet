package com.mangala.wallet.features.send_base.step4.antelope

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.api.model.ChainException
import com.mangala.antelope.base.api.model.tryMapToNotEnoughResourceException
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.antelope.base.domain.model.rpcerror.NotEnoughResourceException
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.token.AntelopeTokenBalance
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.send.AntelopeSendCryptoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.token.GetAntelopeAccountCryptoBalanceUseCase
import com.mangala.wallet.features.chains.antelope_base.presentation.BaseAntelopeTransactScreenModel
import com.mangala.wallet.features.contacts.domain.usecases.GetContactByIdUseCase
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.utils.ext.formatFiat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseAntelopeStep4VerifyAndSendScreenModel(
    protected val contactId: Long?,
    private val senderAccount: String,
    private val toAccount: String,
    blockchainUid: String,
    private val tokenKey: String,
    protected val amount: String,
    private val memo: String,
    private val getContactUseCase: GetContactByIdUseCase,
    private val getAntelopeAccountCryptoBalanceUseCase: GetAntelopeAccountCryptoBalanceUseCase,
    private val getSelectedCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase,
    private val fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    override val transactUseCase: AntelopeSendCryptoUseCase
) : BaseAntelopeTransactScreenModel(
    transactUseCase,
    blockchainUid
) {

    protected val _uiState: MutableStateFlow<BaseAntelopeStep4VerifyAndSendScreenUiState> = MutableStateFlow(BaseAntelopeStep4VerifyAndSendScreenUiState.Loading)
    val uiState: StateFlow<BaseAntelopeStep4VerifyAndSendScreenUiState> = _uiState.asStateFlow()

    protected lateinit var token: AntelopeTokenBalance

    private lateinit var currencySymbol: String

    init {
        screenModelScope.launch {
            val currencyCode = getSelectedCurrencyCodeUseCase()
            currencySymbol = Currency.valueOf(currencyCode).symbol

            val tokens = getAntelopeAccountCryptoBalanceUseCase(accountName = senderAccount, blockchainType, forceRefresh = false)
            tokens.getOrNull()?.find { it.symbol == tokenKey }?.let { token ->
                this@BaseAntelopeStep4VerifyAndSendScreenModel.token = token

                val nativeTokenFiatPrice = getNativeTokenPrice()
                val getTokenFiatValue = nativeTokenFiatPrice?.let { getTokenFiatValue(it, token) }

                val fiatValue = getTokenFiatValue?.formatFiat(currencySymbol).orEmpty()

                _uiState.update {
                    BaseAntelopeStep4VerifyAndSendScreenUiState.Data(
                        contact = contactId?.let { it1 -> getContactUseCase(it1) },
                        recipientAccount = toAccount,
                        selectedToken = token,
                        txHash = null,
                        tokenFiatValue = fiatValue,
                        totalTransactionFiatValue = fiatValue,
                        error = null,
                        isLoading = false
                    )
                }
            } ?: kotlin.run {
                _uiState.update { BaseAntelopeStep4VerifyAndSendScreenUiState.Error(WrappedStringResource.StringRes(MR.strings.message_base_antelope_step4_verify_and_send_screen_model_error_loading_token_data)) }
            }
        }
    }

    private suspend fun getNativeTokenPrice(): BigDecimal? {
        val nativeCoin = getNativeCoinUseCase(blockchainType.uid)
        val nativeCoinPrice = fetchTokenPriceUseCase(forceReload = false, tokenUid = nativeCoin.coinUid, sparkline = false)

        return nativeCoinPrice?.currentPrice?.let { BigDecimal.parseString(it) }
    }

    private fun getTokenFiatValue(nativeTokenPrice: BigDecimal, crypto: AntelopeTokenBalance): BigDecimal {
        val tokenFiatPrice = if (crypto.symbol == "A") {
            nativeTokenPrice
        } else {
            nativeTokenPrice.times(crypto.exchanges.first().price.toBigDecimal())
        }

        return tokenFiatPrice.times(amount.toBigDecimal())
    }

    fun onConsumeTxHash() {
        _uiState.update {
            (it as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data)?.copy(
                txHash = null
            ) ?: it
        }
    }

    final override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
        return transactUseCase.requestSendTransaction(
            blockchainType = blockchainType,
            senderAccountName = senderAccount,
            recipientAccountName = toAccount,
            quantity = Balance(
                amount.toDouble(),
                token.symbol,
                token.decimals
            ),
            memo = memo,
            contract = token.contract
        )
    }

    final override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
        val currentState =
            _uiState.value as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data ?: return

        _uiState.value = currentState.copy(
            resourceRequiredBreakdown = resourceProviderResponse.feeBreakdown,
            resourceRequiredTotal = resourceProviderResponse.fee,
            isLoading = false
        )
    }

    final override fun onRequestTransactionInvalidRequest() {
        _uiState.value = BaseAntelopeStep4VerifyAndSendScreenUiState.Error(WrappedStringResource.StringRes(MR.strings.message_base_antelope_step4_verify_and_send_screen_model_failed_to_buy_sell_ram))
    }

    final override fun onRequestTransactionResourceCovered() {
        val currentState =
            _uiState.value as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data ?: return

        _uiState.value = currentState.copy(promptConfirmTransaction = true, isLoading = false)
    }

    override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
        return transactUseCase.sendToken(
            blockchainType = blockchainType,
            senderAccountName = senderAccount,
            recipientAccountName = toAccount,
            quantity = Balance(
                amount.toDouble(),
                token.symbol,
                token.decimals
            ),
            memo = memo,
            contract = token.contract
        )
    }

    final override fun onConfirmResourceProviderFee() {
        val currentState = _uiState.value as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data ?: return

        _uiState.value = currentState.copy(promptConfirmTransaction = true, isLoading = false)
    }


    final override fun onDismissTransactionFeeBreakdown() {
        val currentState = _uiState.value as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data ?: return

        _uiState.update {
            currentState.copy(
                resourceRequiredBreakdown = null,
                resourceRequiredTotal = null
            )
        }
    }

    final override fun onPinPromptShown() {
        screenModelScope.launch {
            val currentState = _uiState.value as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data ?: return@launch

            _uiState.value = currentState.copy(promptConfirmTransaction = false)
        }
    }

    final override fun showLoadingState() {
        _uiState.update {
            (it as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data)?.copy(
                error = null,
                isLoading = true
            ) ?: it
        }
    }

    final override fun onPushTransactionSuccess(txHash: String) {
        val currentState = _uiState.value as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data ?: return

        _uiState.value = currentState.copy(txHash = txHash, isLoading = false)
    }

    final override fun onPushTransactionFail(throwable: Throwable) {
        val transactionError = getTransactionError(throwable)
        val currentState = _uiState.value as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data ?: return

        _uiState.value = currentState.copy(
            errorDialog = transactionError,
            isLoading = false
        )
    }

    fun onDismissErrorDialog() {
        val currentState = _uiState.value as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data ?: return
        _uiState.value = currentState.copy(errorDialog = null)
    }

    private fun getTransactionError(throwable: Throwable): TransactionError {
        if (throwable is ChainException) {
            val resourceException = throwable.tryMapToNotEnoughResourceException()
            when (resourceException) {
                is NotEnoughResourceException.NotEnoughRamException -> {
                    // Calculate additional RAM needed in KB (1 KB = 1024 bytes)
                    val additionalBytesNeeded = resourceException.neededBytes - resourceException.hasBytes
                    val additionalKbNeeded = additionalBytesNeeded / 1024.0

                    return TransactionError(
                        message = WrappedStringResource.StringRes(
                            MR.strings.message_base_antelope_step4_verify_and_send_screen_model_insufficient_ram,
                            additionalKbNeeded
                        ),
                        type = TransactionErrorType.INSUFFICIENT_RAM,
                        throwable = throwable
                    )
                }
                is NotEnoughResourceException.NotEnoughCpuException -> {
                    return TransactionError(
                        message = WrappedStringResource.PlainString(throwable.message ?: "Insufficient CPU"),
                        type = TransactionErrorType.INSUFFICIENT_CPU,
                        throwable = throwable
                    )
                }
                is NotEnoughResourceException.NotEnoughNetException -> {
                    return TransactionError(
                        message = WrappedStringResource.PlainString(throwable.message ?: "Insufficient NET"),
                        type = TransactionErrorType.INSUFFICIENT_NET,
                        throwable = throwable
                    )
                }
                else -> {}
            }
        }

        val message = throwable.message?.let {
            WrappedStringResource.PlainString(it)
        } ?: WrappedStringResource.StringRes(MR.strings.message_base_antelope_step4_verify_and_send_screen_model_failed_to_send_transaction)

        return TransactionError(
            message = message,
            type = TransactionErrorType.GENERIC,
            throwable = throwable
        )
    }
}