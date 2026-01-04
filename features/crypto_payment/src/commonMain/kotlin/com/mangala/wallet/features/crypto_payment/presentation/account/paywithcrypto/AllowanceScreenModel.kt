package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.token.usecases.GetTokenByIdUseCase
import com.mangala.wallet.domain.transaction.fee.TransactionFeeType
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EstimateGasUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetLatestBlockUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetRecommendedGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionFeeOptionsUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.ui.SendTransactionScreenModel
import com.mangala.wallet.features.crypto_payment.SMART_CONTRACT_NATIVE_COIN_ADDRESS
import com.mangala.wallet.features.crypto_payment.WALLET_NATIVE_COIN_ADDRESS
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.ApproveAllowanceUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.GetCryptoPaymentContractAddressUseCase
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.utils.ext.ethToWei
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AllowanceScreenModel(
    private val minimumAllowanceRequired: BigDecimal,
    private val paidAccountId: String,
    fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    getNativeCoinUseCase: GetNativeCoinUseCase,
    getTransactionFeeOptionsUseCase: GetTransactionFeeOptionsUseCase,
    getTokenBalanceByTokenIdUseCase: GetTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase: GetRecommendedGasPriceUseCase,
    getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getLatestBlockUseCase: GetLatestBlockUseCase,
    private val getTokenByIdUseCase: GetTokenByIdUseCase,
    private val approveAllowanceUseCase: ApproveAllowanceUseCase,
    private val getCryptoPaymentContractAddressUseCase: GetCryptoPaymentContractAddressUseCase,
    private val estimateGasUseCase: EstimateGasUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase
): SendTransactionScreenModel(
    fetchTokenPriceUseCase,
    getNativeCoinUseCase,
    getTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase,
    getTransactionFeeOptionsUseCase,
    getCurrentCurrencyCodeUseCase,
    getLatestBlockUseCase
) {

    private var _uiState: MutableStateFlow<AllowanceScreenUiState> =
        MutableStateFlow(AllowanceScreenUiState.Success)
    val uiState: StateFlow<AllowanceScreenUiState> = _uiState.asStateFlow()

    private var paidAccount: AccountModel = getAccountByIdUseCase(paidAccountId)

    init {
        _uiState.value = AllowanceScreenUiState.Success
    }

    fun isValidAllowance(allowanceString: String?): Boolean {
        val allowanceStringTrimmed = allowanceString?.trim() ?: return false
        return if (isValidBigDecimalFormat(allowanceStringTrimmed)) {
            val allowance = BigDecimal.parseString(allowanceStringTrimmed)
            allowance >= minimumAllowanceRequired
        } else {
            false
        }
    }

    private fun isValidBigDecimalFormat(allowanceString: String): Boolean {
        val bigDecimalRegex = Regex("^[+-]?\\d+(\\.\\d+)?([eE][+-]?\\d+)?$")
        return bigDecimalRegex.matches(allowanceString)
    }

    fun approveAllowanceAndCreateAccount(amountApproved: BigDecimal, tokenId: Long, tokenAddress: String) {
        screenModelScope.launch {
            _uiState.value = AllowanceScreenUiState.Loading
            val tokenDecimal = getTokenByIdUseCase(tokenId).first().decimals ?: run {
                _uiState.value = AllowanceScreenUiState.Error(
                    WrappedStringResource.StringRes(
                        MR.strings.error_pay_with_crypto_can_not_get_token_decimals
                    )
                )
                return@launch
            }
            val currentAccountAddress = paidAccount.bip44Address
            val tokenPaid = getTokenPaid(tokenAddress)
            sendTransactionApproveAllowance(
                amountApproved,
                tokenDecimal,
                currentAccountAddress,
                Address(tokenPaid)
            )?.let {
                println("approve allowance tx hash: $it")
                _uiState.value = AllowanceScreenUiState.Approved
            } ?: run {
                _uiState.value = AllowanceScreenUiState.Error(
                    WrappedStringResource.StringRes(
                        MR.strings.error_approve_allowance_failed
                    )
                )
                return@launch
            }
        }
    }

    private suspend fun sendTransactionApproveAllowance(
        amountApproved: BigDecimal,
        tokenDecimal: Long,
        accountAddress: String,
        tokenAddress: Address
    ): String? {
        val amountApprovedWei = amountApproved.ethToWei(tokenDecimal.toInt())
        val transactionFeeOptions = getTransactionFeeOptions()
        val transactionData = approveAllowanceUseCase.buildTransactionData(
            tokenAddress = tokenAddress,
            amount = amountApprovedWei.toBigInteger(),
            spender = getCryptoContractAddressForPayment()
        )
        println("transaction data approve allowance: $transactionData")
        val gasEstimated = estimateGasUseCase(
            url = rpcUrl,
            id = currentId.getAndIncrement(),
            from = Address(accountAddress),
            to = transactionData.to,
            amount = transactionData.value,
            gasPrice = getPreferredGasPrice(transactionFeeOptions?.find { it.transactionFeeType == TransactionFeeType.REGULAR }),
            transactionData = transactionData
        ) ?: run {
            return null
        }
        println("gas estimated: $gasEstimated")
        return approveAllowanceUseCase(
            blockchainType = blockchainType,
            from = Address(accountAddress),
            gasPrice = getPreferredGasPrice(transactionFeeOptions?.find { it.transactionFeeType == TransactionFeeType.REGULAR }),
            gas = gasEstimated,
            transactionData = transactionData
        )
    }

    private fun getTokenPaid(token: String): String {
        return if (token == WALLET_NATIVE_COIN_ADDRESS) {
            SMART_CONTRACT_NATIVE_COIN_ADDRESS
        } else {
            token
        }
    }

    private fun getCryptoContractAddressForPayment(): Address {
        return getCryptoPaymentContractAddressUseCase.invoke(Chain.fromBlockchainType(blockchainType))
    }
}

sealed class AllowanceScreenUiState {
    data object Loading : AllowanceScreenUiState()
    data object Success : AllowanceScreenUiState()
    data object Approved : AllowanceScreenUiState()
    data class Error(val message: WrappedStringResource) : AllowanceScreenUiState()
}