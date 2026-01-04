package com.mangala.wallet.utils

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

actual fun getThousandSeparator(): String {
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterDecimalStyle
    }
    val example = formatter.stringFromNumber(NSNumber(1234)) ?: "1,234"
    return (example.firstOrNull { it.isDigit().not() } ?: ',').toString()
}