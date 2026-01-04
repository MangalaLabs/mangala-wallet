package com.mangala.wallet.utils.ext

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.utils.InfoUnit

fun Long.toBigDecimal() = BigDecimal.fromLong(this)

fun Long?.orZero() = this ?: 0L

fun Long?.toBoolean() = this == 1L

/*
    * Formats the given long value to a human readable format.
    * @param unit The unit to use for formatting.
    * @returns A pair containing the formatted value and the symbol of the unit.
 */
fun Long.formatBytes(unit: InfoUnit = InfoUnit.findSuitableUnit(this)): Pair<Double, InfoUnit> {
    return (this.toDouble() / unit.bytes) to unit
}

fun Long.bytesToKilobytes() = this.toDouble() / InfoUnit.KILOBYTE.bytes

fun Long.millisecondsToSeconds() = (this.toDouble() / 1000).toLong()