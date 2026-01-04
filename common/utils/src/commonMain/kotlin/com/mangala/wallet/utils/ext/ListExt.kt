package com.mangala.wallet.utils.ext

import com.ionspin.kotlin.bignum.decimal.BigDecimal

fun <T> List<T>.sumOf(selector: (T) -> BigDecimal): BigDecimal {
    return this.fold(BigDecimal.ZERO) { acc, value -> acc + selector(value) }
}
