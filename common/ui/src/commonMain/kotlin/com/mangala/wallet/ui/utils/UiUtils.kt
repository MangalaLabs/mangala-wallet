package com.mangala.wallet.ui.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.utils.CalBalance
import com.mangala.wallet.utils.ext.formatCompact

fun TokenBalanceModel.formattedCompactBalance(): String {
    val divResult = (BigDecimal.parseString(balance).divide(
        BigDecimal.TEN.pow(contractDecimals.toInt()),
        DecimalMode(20L, RoundingMode.TOWARDS_ZERO) // Round towards zero as we don't want user to see they have more coins than they really own :)
    ))

    return divResult.formatCompact() + " " + contractSymbol
}

fun TokenBalanceModel.formattedBalance24h(): String {
    return CalBalance.formatBalance(
        balance24h,
        contractDecimals,
        contractSymbol
    )
}

const val PNL_DECIMAL_PLACES = 2L