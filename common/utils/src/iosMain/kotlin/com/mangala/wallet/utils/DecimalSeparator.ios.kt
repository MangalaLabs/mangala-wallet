package com.mangala.wallet.utils

import platform.Foundation.NSLocale
import platform.Foundation.NSNumberFormatter
import platform.Foundation.currentLocale

actual fun getCurrentLocaleDecimalSeparator(): Char {
    val currentLocale = NSLocale.currentLocale
    val formatter = NSNumberFormatter()
    formatter.locale = currentLocale
    return formatter.decimalSeparator.firstOrNull() ?: '.'
}