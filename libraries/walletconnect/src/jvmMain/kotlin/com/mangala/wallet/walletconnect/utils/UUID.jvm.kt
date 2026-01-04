/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.utils

import java.util.UUID

internal actual object UUID {
    actual fun randomUUID(): String {
        return UUID.randomUUID().toString()
    }
}
