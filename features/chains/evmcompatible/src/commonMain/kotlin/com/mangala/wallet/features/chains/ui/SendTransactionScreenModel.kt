package com.mangala.wallet.features.chains.ui

import cafe.adriel.voyager.core.concurrent.AtomicInt32
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.transaction.fee.TransactionFeeOption
import com.mangala.wallet.domain.transaction.fee.TransactionFeeType
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetLatestBlockUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetRecommendedGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionFeeOptionsUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.ext.toBigDecimal
import com.mangala.wallet.utils.ext.weiToEth
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.random.Random

/*
    To use this class, please remember to set accountId and blockchainType in init block
 */
abstract class SendTransactionScreenModel(
    private val fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase,
    private val getTokenBalanceByTokenIdUseCase: GetTokenBalanceByTokenIdUseCase,
    private val getRecommendedGasPriceUseCase: GetRecommendedGasPriceUseCase,
    private val getTransactionFeeOptionsUseCase: GetTransactionFeeOptionsUseCase,
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val getLatestBlockUseCase: GetLatestBlockUseCase
): BaseScreenModel() {

    protected val currentId = AtomicInt32(Random.nextInt(100))

    lateinit var accountId: String
    var gasPrice: GasPrice = GasPrice.Legacy(10_000_000_000)
        protected set
    var gasLimitInWei: BigDecimal = BigDecimal.ZERO
        protected set
    var blockchainType: BlockchainType = BlockchainType.BinanceSmartChainTestNet
        protected set
    protected val rpcUrl
        get() = blockchainType.getRpcUrl().first()
    protected val chain
        get() = Chain.fromBlockchainType(blockchainType)

    protected val nativeCoin by lazy {
        getNativeCoinUseCase(blockchainType.uid)
    }

    private val nativeCoinBalance by lazy {
        getTokenBalanceByTokenIdUseCase(nativeCoin.id, accountId).first()
    }

    protected fun getPreferredGasPrice(transactionFeeOption: TransactionFeeOption?): GasPrice {
        val result = when (val gasPrice = gasPrice) {
            is GasPrice.Legacy -> {
                val newLegacyGasPrice =
                    transactionFeeOption?.gasPrice?.longValue(
                        exactRequired = false
                    ) ?: 0  // as we multiply by multiplier resulting gas price might not be whole numbers
                gasPrice.copy(newLegacyGasPrice)
            }

            is GasPrice.Eip1559 -> {
                val newMaxPriorityFeePerGas = transactionFeeOption?.priorityFee?.longValue(exactRequired = false) ?: 0
                val newBaseFee = transactionFeeOption?.baseFee?.longValue(exactRequired = false) ?: 0
                val newMaxFeePerGas = transactionFeeOption?.maxGas?.longValue(exactRequired = false) ?: 0
                gasPrice.copy(
                    maxFeePerGas = newMaxFeePerGas,
                    maxPriorityFeePerGas = newBaseFee,
                    baseFee = newMaxPriorityFeePerGas
                )
            }
        }
        return result
    }

    private suspend fun getTransactionFeeFiatValue(): BigDecimal? {
        nativeCoin.let {
            val tokenPrice = fetchTokenPriceUseCase(
                false,
                mapOf(it.coinUid to nativeCoinBalance),
                false
            ).firstOrNull() ?: return null

            val currentPrice = tokenPrice.currentPrice ?: "0"
            return BigDecimal.parseString(currentPrice)
        }
    }

    protected suspend fun getTransactionFeeOptions(): List<TransactionFeeOption>? = coroutineScope {
        val recommendedGasPriceCalculation = async {
            getRecommendedGasPriceUseCase(
                chain,
                rpcUrl,
                currentId.getAndIncrement()
            )
        }

        gasPrice = recommendedGasPriceCalculation.await().getOrNull()
            ?: return@coroutineScope null // TODO: Handle cannot get recommended gas price

        return@coroutineScope getTransactionFeeOptionsUseCase(gasPrice)
    }

    protected suspend fun getTransactionFeeOptionUiModels(
        transactionFeeOptions: List<TransactionFeeOption>,
        transactionFeeType: TransactionFeeType,
        gasLimitRawValue: BigDecimal
    ) = coroutineScope {
        val priceCalculation = async { getTransactionFeeFiatValue() }
        val price = priceCalculation.await() ?: return@coroutineScope null // TODO: Handle cannot get native coin price

        val currencyCode = getCurrentCurrencyCodeUseCase()
        val currencySymbol = Currency.valueOf(currencyCode).symbol

        val decimal = nativeCoin.decimals ?: 0

        transactionFeeOptions.map {
            val gasPrice = it.maxGas.weiToEth(decimal.toInt())
            val transactionFeeValue = gasLimitRawValue.multiply(gasPrice)
            val transactionFeeFiatValue = transactionFeeValue.multiply(price)

            EvmFeeOptionUiModel(
                transactionFee = it,
                isSelected = it.transactionFeeType == transactionFeeType,
                decimals = decimal.toInt(),
                transactionFeeValue = transactionFeeValue,
                transactionFeeFiatValue = transactionFeeFiatValue,
                symbol = nativeCoinBalance.contractSymbol,
                fiatCurrencySymbol = currencySymbol
            )
        }
    }

    // https://github.com/MetaMask/core/blob/30b69ff406be64b5eb40f5b33363b4d794467adb/packages/transaction-controller/src/TransactionController.ts#L1077
    protected suspend fun Long.getBufferedGasLimit(): Long {
        // TODO: Can batch fetch latest block and gas limit into 1 batch JSON RPC call
        val estimatedGasLimit = this.toBigDecimal()
        val latestBlock = getLatestBlockUseCase(rpcUrl, currentId.getAndIncrement())

        return latestBlock?.let {
            val latestBlockGasLimit = it.gasLimit.toBigDecimal()
            val maxGasLimit = latestBlockGasLimit.multiply(BigDecimal.parseString("0.9"))
            val bufferedGasLimit = estimatedGasLimit.multiply(GAS_LIMIT_MULTIPLIER.toBigDecimal())

            if (estimatedGasLimit > maxGasLimit) {
                return estimatedGasLimit.longValue(exactRequired = false)
            }
            if (bufferedGasLimit < maxGasLimit) {
                return bufferedGasLimit.longValue(exactRequired = false)
            }
            return maxGasLimit.longValue(exactRequired = false)
        } ?: estimatedGasLimit.longValue(exactRequired = false)
    }

    protected fun getCurrentFeeOptionUiModel(
        transactionFeeOptions: List<EvmFeeOptionUiModel>,
        transactionFeeType: TransactionFeeType
    ): EvmFeeOptionUiModel? {
        return transactionFeeOptions.firstOrNull { it.transactionFee.transactionFeeType == transactionFeeType }
            ?: transactionFeeOptions.firstOrNull { it.transactionFee.transactionFeeType == TransactionFeeType.REGULAR }
    }

    companion object {
        private const val GAS_LIMIT_MULTIPLIER = 1.5
    }
}