package com.mangala.wallet.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.wallet.utils.ext.format

fun formatCurrencyAmount(amount: BigDecimal, coinUid: String): String {
    val decimalPlaces = getDecimalPlaces(coinUid)
    return amount.format(decimalPlaces.toLong(), RoundingMode.ROUND_HALF_TO_EVEN)
}

private fun getDecimalPlaces(coinUid: String): Int {
    return when (coinUid.uppercase()) {
        "ETHEREUM" -> 6
        "BITCOIN" -> 8
        "BINANCECOIN" -> 4
        else -> 2
    }
}