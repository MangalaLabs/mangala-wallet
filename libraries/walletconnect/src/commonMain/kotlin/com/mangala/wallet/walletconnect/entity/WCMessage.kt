/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.entity

data class WCMessage(
    val connectionId: String,
    val method: WCMethod
)
