package com.mangala.wallet.utils

import android.icu.text.DecimalFormatSymbols

actual fun getThousandSeparator(): String {
    return DecimalFormatSymbols.getInstance().groupingSeparator.toString()
}