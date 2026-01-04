package com.mangala.wallet.model.token.domain

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.wallet.utils.calculatingDecimalMode
import com.mangala.wallet.utils.ext.formatFiat
import dev.icerock.moko.resources.ImageResource

data class TokenBalanceModel(
    val tokenId: Long,
    val accountId: String,
    val totalAmount: Double,
    val balance: String,
    val balance24h: String,
    val balanceLocked: String,
    val orderNumber: Int,
    val contractDecimals: Long,
    val contractName: String,
    val contractSymbol: String,
    val contractAddress: String,
    val logoUrl: String,
    val localImage: ImageResource?,
    val coinUid: String,
    val currencyCode: String,
    val currentPrice: String?,
    val marketCap: String?,
    val marketCapRank: Int?,
    val totalVolume: String?,
    val high24h: String?,
    val low24h: String?,
    val priceChange24h: String?,
    val priceChangePercentage24h: String?,
    val priceChangePercentage7d: String?,
    val marketCapChange24h: String?,
    val marketCapChangePercentage24h: String?,
    val sparklineIn7d: Sparkline?
) {
    data class Sparkline(val price: List<Double>?)

    val yesterdaysValue: BigDecimal? = if (currentPrice != null && priceChange24h != null) {
        if (balance24h.isEmpty()) {
            null
        } else {
            val yesterdaysBalance = BigDecimal.parseString(balance24h).divide(BigDecimal.TEN.pow(contractDecimals.toInt()), calculatingDecimalMode)
            val currentPriceBigDecimal = BigDecimal.parseString(currentPrice)
            val priceChange24hBigDecimal = BigDecimal.parseString(priceChange24h)
            val oldPrice = currentPriceBigDecimal - priceChange24hBigDecimal
            yesterdaysBalance * oldPrice
        }
    } else null

    val todaysValue: BigDecimal? = if (currentPrice != null) {
        val newBalance = BigDecimal.parseString(balance).divide(BigDecimal.TEN.pow(contractDecimals.toInt()), calculatingDecimalMode)
        val currentPriceBigDecimal = BigDecimal.parseString(currentPrice)
        newBalance * currentPriceBigDecimal
    } else null

    val pnl: BigDecimal? = if (yesterdaysValue != null && todaysValue != null) {
        todaysValue - yesterdaysValue
    } else null

    val isCoin = contractAddress == "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
}

fun TokenBalanceModel.formattedBalance(): String {
    return formattedBalanceForHuman() + " " + contractSymbol
}

fun TokenBalanceModel.formattedBalanceForHuman(): String {
    return (BigDecimal.parseString(balance).divide(BigDecimal.TEN.pow(contractDecimals.toInt()), DecimalMode(10.toLong(), RoundingMode.ROUND_HALF_CEILING))).scale(4).toStringExpanded()
}

fun TokenBalanceModel.formattedValue(currencySymbol: String): String {
    return (todaysValue ?: BigDecimal.ZERO).formatFiat(
        currencySymbol,
        decimalPlaces = 2
    ) // TODO: Handle decimal places based on currency
}

fun List<TokenBalanceModel>?.totalValue(): BigDecimal? {
    if (this == null) return null

    var value = BigDecimal.ZERO
    this.forEach {
        value += it.todaysValue ?: BigDecimal.ZERO
    }
    return value
}

fun List<TokenBalanceModel>?.totalValueOrZero(): BigDecimal {
    return totalValue() ?: BigDecimal.ZERO
}