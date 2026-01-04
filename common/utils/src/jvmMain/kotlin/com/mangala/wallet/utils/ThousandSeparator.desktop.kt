package com.mangala.wallet.utils

import java.text.DecimalFormatSymbols

actual fun getThousandSeparator(): String {
    return DecimalFormatSymbols.getInstance().groupingSeparator.toString()
}