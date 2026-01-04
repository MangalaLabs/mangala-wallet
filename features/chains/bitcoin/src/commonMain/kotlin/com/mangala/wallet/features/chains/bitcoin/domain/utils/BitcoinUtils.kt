package com.mangala.wallet.features.chains.bitcoin.domain.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.wallet.utils.removeTrailingZeroes

fun Long.formatBitcoin(): String {
    val bitcoinValue = BigDecimal.fromLong(this).divide(BigDecimal.fromLong(100_000_000), DecimalMode.DEFAULT.copy(decimalPrecision = 8, RoundingMode.ROUND_HALF_CEILING))
    
    val formatted = bitcoinValue.removeTrailingZeroes().toPlainString()
    
    return "$formatted BTC"
}

fun BigDecimal.btcToSatoshis(): BigDecimal {
    return this.multiply(BigDecimal.fromLong(100_000_000))
}

fun BigDecimal.satoshisToBtc(): BigDecimal {
    return this.divide(BigDecimal.fromLong(100_000_000), decimalMode = DecimalMode.DEFAULT.copy(decimalPrecision = 20, scale = 8, roundingMode = RoundingMode.ROUND_HALF_TO_EVEN))
}