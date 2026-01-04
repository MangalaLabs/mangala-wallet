/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect

import com.mangala.wallet.walletconnect.utils.toUrlEncode
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlEncodeTest {
    @Test
    fun encode() {
        val url ="https://safe-walletconnect.gnosis.io"
        assertEquals(
            "https%3A%2F%2Fsafe-walletconnect.gnosis.io",
            url.toUrlEncode()
        )
    }
}
