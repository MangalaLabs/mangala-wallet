package com.mangala.wallet.utils.ext

import com.mangala.wallet.utils.InfoUnit

fun Double.kilobytesToBytes() = this * InfoUnit.KILOBYTE.bytes

fun Double.formatWithSign() = if (this < 0.0) {
    this.toString()
} else {
    "+$this"
}

fun Double?.orZero() = this ?: 0.0