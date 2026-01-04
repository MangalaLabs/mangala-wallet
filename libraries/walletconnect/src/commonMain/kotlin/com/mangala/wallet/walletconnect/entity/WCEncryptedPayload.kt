/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class WCEncryptedPayload(
    @SerialName("data") val data: String,
    @SerialName( "iv") val iv: String,
    @SerialName("hmac") val hmac: String
)
