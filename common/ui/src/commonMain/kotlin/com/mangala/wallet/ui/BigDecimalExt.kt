package com.mangala.wallet.ui

import com.ionspin.kotlin.bignum.decimal.BigDecimal

fun List<BigDecimal>.sumOf(): BigDecimal = if (this.isEmpty()) BigDecimal.ZERO else this.reduce(BigDecimal::add)

inline fun <T> Iterable<T>.sumOf(selector: (T) -> BigDecimal): BigDecimal {
    var sum = BigDecimal.ZERO
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
