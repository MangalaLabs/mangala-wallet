/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.utils

import platform.Foundation.NSCharacterSet
import platform.Foundation.NSString
import platform.Foundation.URLHostAllowedCharacterSet
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters

@Suppress("CAST_NEVER_SUCCEEDS")
internal actual fun String.toUrlEncode(): String{
    return (this as NSString).stringByAddingPercentEncodingWithAllowedCharacters(
        NSCharacterSet.URLHostAllowedCharacterSet,
    ) ?: this
}
