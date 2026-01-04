/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.entity

import kotlinx.serialization.Serializable

@Serializable
data class WCConnection(
    val id: String,
    val peerId: String,
    val config: WCSessionConfig,
    val clientId: String,
    val clientMeta: WCPeerMeta,
    val peerMeta: WCPeerMeta?,
    val accounts: List<String>,
    val chainId: Long,
)
