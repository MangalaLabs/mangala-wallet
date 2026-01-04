package com.mangala.wallet.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal

fun String.toBigDecimalOrNull(): BigDecimal? {
    return try {
        this.toBigDecimal()
    } catch (e: Exception) {
        null
    }
}

fun String?.isNotNullOrBlank(): Boolean {
    return this.isNullOrBlank().not()
}

fun String.formatAmountInput(oldValue: String, maxDecimals: Int = 18): String {
    return if (this.isEmpty()) {
        this
    } else {
        val trimmedNewAmount = this
            .trim()
            .replace(",+".toRegex(), ".")
            .replace("\\.+".toRegex(), ".")

        val newAmount = if (trimmedNewAmount.contains(".") && trimmedNewAmount.substringAfter(".").length > maxDecimals) {
            if (oldValue.substringAfter(".").length > maxDecimals) {
                oldValue.substringBefore(".") + "." + oldValue.substringAfter(".").substring(0, maxDecimals)
            } else {
                oldValue
            }
        } else {
            trimmedNewAmount
        }

        when (val result = newAmount.replace(",", ".").toBigDecimalOrNull()) {
            null -> oldValue
            else -> {
                if (this.endsWith(".") || this.endsWith(",")) {
                    newAmount
                } else if ((this.contains(".") || this.contains(",")) && newAmount.last() == '0') {
                    newAmount
                } else {
                    result.toPlainString()
                }
            }
        }
    }
}

fun String.formattedAddress(leadingCharsCount: Int = 6, trailingCharsCount: Int = 4) = this.take(leadingCharsCount) + "..." + this.takeLast(trailingCharsCount)

fun String.isSignificantValue() = this.isNotBlank() && this.toDoubleOrNull() != null && this.toDouble() > 0

fun String.toBigDecimalOrZero(): BigDecimal {
    return this.toBigDecimalOrNull() ?: BigDecimal.ZERO
}

fun String.isDecimalPlace(): Boolean {
    return this == "." || this == ","
}