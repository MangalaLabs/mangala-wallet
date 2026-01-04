/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SocketMessage(
    val topic: String,
    val type: Type,
    val payload: String
) {
    @Serializable
    enum class Type{
        @SerialName("pub")
        Pub,
        @SerialName("sub")
        Sub
    }
}
