package com.mangala.wallet.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal

fun max(a: BigDecimal, b: BigDecimal): BigDecimal {
    if (a > b) {
        return a
    } else {
        return b
    }
}

fun min(a: BigDecimal, b: BigDecimal): BigDecimal {
    if (a < b) {
        return a
    } else {
        return b
    }
}