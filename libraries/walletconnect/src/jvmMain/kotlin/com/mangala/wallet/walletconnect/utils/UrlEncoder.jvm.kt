/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.utils

import java.net.URLEncoder
import java.nio.charset.Charset

internal actual fun String.toUrlEncode(): String{
    return URLEncoder.encode(this, "UTF-8")
}
