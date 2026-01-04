package com.mangala.features.wallet.presentation

import androidx.compose.ui.graphics.Color
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AntelopeSymbolUtils
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.provider.coingecko.CoinGeckoTokenPriceModel
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.token.AntelopeTokenBalance
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AntelopeAccountBalanceUnit
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.InfoUnit
import com.mangala.wallet.utils.ext.formatBytes
import com.mangala.wallet.utils.ext.formatCompact
import com.mangala.wallet.utils.ext.formatFiat
import dev.icerock.moko.resources.ImageResource

data class AntelopeAccountItemUiModel(
    val account: AntelopeAccount,
    override val isBalanceVisible: Boolean,
    override val currencySymbol: String,
    val coreBalanceValue: BigDecimal?,
    val coreBalanceSymbol: String?,
    val stakedCpu: BigDecimal?,
    val stakedNet: BigDecimal?,
    val stakedInRex: BigDecimal?,
    val cpuUsagePercentage: Double?,
    val netUsagePercentage: Double?,
    val ramBalanceBytes: Long?,
    val ramPriceKilobytes: BigDecimal?,
    val nativeCoinPnl: BigDecimal?,
    val nativeCoinPrice: BigDecimal?,
    val totalValueInNativeCoin: BigDecimal?,
    val assets: List<AntelopeAssetsUiModel>?,
    val selectedCoreBalanceUnit: AntelopeAccountBalanceUnit = AntelopeAccountBalanceUnit.NativeCoin,
    val coinGeckoExchangeRate: CoinGeckoTokenPriceModel?,
    val isLoading: Boolean
    // Add data for CPU, NET
) : BaseAccountItemUiModel {
    private val decimalFormat = DecimalFormat("#.###")
    private val ramBalance: String?
        get() {
            val formattedValue = ramBalanceBytes?.formatBytes()
            return formattedValue?.let { "${decimalFormat.format(it.first)} ${it.second.symbol}" }
        }
    private val totalValue = (if (selectedCoreBalanceUnit == AntelopeAccountBalanceUnit.NativeCoin)
        totalValueInNativeCoin
    else
        coinGeckoExchangeRate?.data?.get(selectedCoreBalanceUnit.currencySymbol)?.let {
            totalValueInNativeCoin?.times(it)
        })?.formatCompact(3)


    val coreBalanceSymbolSkeletonVisible = coreBalanceSymbol == null
    val formattedCoreBalanceSymbol =
        if (selectedCoreBalanceUnit == AntelopeAccountBalanceUnit.NativeCoin)
            coreBalanceSymbol ?: "EOS" // placeholder for skeleton
        else
            selectedCoreBalanceUnit.symbol

    val totalValueSkeletonVisible = assets?.any { it.isLoadingBalance } ?: true
    val totalValueFormatted: String
        get() {
            return if (totalValueSkeletonVisible) {
                "0.0000" // placeholder for balance skeleton
            } else if (isBalanceVisible) {
                totalValue.orEmpty()
            } else {
                HIDDEN_BALANCE_STRING
            }
        }

    private val nativeCoinPnlParsed = nativeCoinPnl?.toStringExpanded()
    private val nativeCoinPnlSign = if (nativeCoinPnl?.isPositive == true) "+" else ""
    override val formattedPnl =
        if (nativeCoinPnl != null) "$nativeCoinPnlSign$nativeCoinPnlParsed%" else ""
    val nativeCoinPnlColor: Color
        get() {
            return if (nativeCoinPnl == null || nativeCoinPnl == BigDecimal.ZERO) Colors.gray else if (nativeCoinPnl > BigDecimal.ZERO) Color(
                0xFF00A699
            ) else Colors.coral // TODO: Refactor to common function
        }

    private val fiatValue: String? =
        if (nativeCoinPrice == null || totalValueInNativeCoin == null) null else nativeCoinPrice.multiply(
            totalValueInNativeCoin
        ).formatFiat(currencySymbol)
    val fiatValueSkeletonVisible = assets?.any { it.isLoadingPrice } ?: true
    val fiatValueFormatted =
        if (fiatValueSkeletonVisible) "\$0.00" else if (isBalanceVisible) fiatValue.orEmpty() else HIDDEN_BALANCE_STRING

    private val ramBalanceSkeletonVisible = account.isTemp.not() && ramBalance.isNullOrEmpty()
    val ramBalanceFormatted =
        if (ramBalanceSkeletonVisible) "0.00 KB" else if (isBalanceVisible) ramBalance.orEmpty() else HIDDEN_BALANCE_STRING
}

sealed interface AntelopeAssetsUiModel {
    val key: String
    val name: String
    val symbol: String
    val balance: BigDecimal?
    val priceInNativeCoin: BigDecimal?
    val formattedPrice: String?
    val sparkline: List<Double>?
    val priceChangePercentage24h: BigDecimal?
    val isLoadingBalance get() = key.contains("placeholder") || balance == null || priceInNativeCoin == null || formattedPrice == null
    val isLoadingPrice get() = isLoadingBalance || priceInNativeCoin == null || formattedPrice == null

    data class RamBalanceUiModel(
        override val key: String,
        override val sparkline: List<Double>,
        override val balance: BigDecimal?, // Total RAM in bytes
        val ramUsed: BigDecimal,
        override val priceChangePercentage24h: BigDecimal?,
        val ramPriceInKilobytes: BigDecimal?,
        val nativeCoinSymbol: String,
        val accountName: String, // Convenience value for handling click on RAM asset
        val blockchainType: BlockchainType? = null
    ) : AntelopeAssetsUiModel {
        private val decimalFormat = DecimalFormat("#.##")
        val ramUsedPercentage: Float?
            get() {
                val result = when (balance) {
                    null -> null
                    BigDecimal.ZERO -> {
                        0.0f
                    }
                    else -> {
                        ramUsed.longValue(false).toDouble().div(balance.longValue(false)).times(100)
                            .toFloat()
                    }
                }

                return result
            }

        private val convertedRamQuota = balance?.longValue(true)?.formatBytes()
        val convertedBalance = convertedRamQuota?.first?.let { decimalFormat.format(it) }
        private val unit: InfoUnit? = convertedRamQuota?.second
        val unitFormatted = unit?.symbol

        override val priceInNativeCoin: BigDecimal? = ramPriceInKilobytes?.times(InfoUnit.KILOBYTE.bytes) // Convert back to bytes so that amount * price = total value
        override val name: String = "RAM"
        override val symbol: String = "RAM"
        override val formattedPrice = if (ramPriceInKilobytes == null) null else {
            val formattedSymbol = AntelopeSymbolUtils.formatSymbol(nativeCoinSymbol, blockchainType)
            "${ramPriceInKilobytes.formatCompact()} $formattedSymbol/KB"
        }
    }

    data class TokenBalanceUiModel(
        override val key: String,
        override val sparkline: List<Double>?,
        override val name: String,
        override val symbol: String,
        override val balance: BigDecimal?,
        override val priceInNativeCoin: BigDecimal?,
        override val priceChangePercentage24h: BigDecimal?,
        val nativeCoinPrice: BigDecimal?,
        val iconResource: ImageSource?,
        val fiatSymbol: String
    ) : AntelopeAssetsUiModel {
        override val formattedPrice = if (priceInNativeCoin == null || nativeCoinPrice == null) null else priceInNativeCoin.times(nativeCoinPrice).formatFiat(fiatSymbol)
    }
}

fun AntelopeTokenBalance.AntelopeTokenMetadata.toImageSource(): ImageSource {
    return localImage?.let { ImageSource.Resource(it) } ?: ImageSource.Url(logo)
}
