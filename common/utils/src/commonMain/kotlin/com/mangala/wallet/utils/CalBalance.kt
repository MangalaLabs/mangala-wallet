package com.mangala.wallet.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode

object CalBalance {

    fun formatBalance(balance: String, decimal: Long, symbol: String): String {
        return DecimalFormat("#,##0.00000").format(
            calBalance(balance, decimal).toString().toDouble()
        ) + " $symbol"
    }

    fun calBalance(balance: String, decimal: Long): BigDecimal {
        val amount = BigDecimal.parseString(balance)
            .divide(
                BigDecimal.TEN.pow(decimal),
                DecimalMode(decimal, RoundingMode.ROUND_HALF_TO_EVEN)
            )

        return amount
    }

    fun formatBalance(balance: String, decimal: Long, decimalPlaces: Int): String {
        return DecimalFormat("#,##0.${"0".repeat(decimalPlaces)}").format(
            calBalance(
                balance,
                decimal
            ).toString().toDouble()
        )
    }

}