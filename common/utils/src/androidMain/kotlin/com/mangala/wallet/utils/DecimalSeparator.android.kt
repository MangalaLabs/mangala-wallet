package com.mangala.wallet.utils

import java.text.DecimalFormatSymbols
import java.util.Locale

actual fun getCurrentLocaleDecimalSeparator(): Char {
    val currentLocale = Locale.getDefault()
    return DecimalFormatSymbols(currentLocale).decimalSeparator
}