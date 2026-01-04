/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.entity

import kotlinx.serialization.Serializable

internal const val JSONRPC_VERSION = "2.0"

@Serializable
internal data class JsonRpcRequest<T>(
    val id: Long,
    val jsonrpc: String,
    val method: String?,
    val params: T
)

@Serializable
internal data class JsonRpcResponse<T>(
    val jsonrpc: String,
    val id: Long,
    val result: T?
)

@Serializable
internal data class JsonRpcErrorResponse(
    val jsonrpc: String,
    val id: Long,
    val error: JsonRpcError
)

@Serializable
internal data class JsonRpcError(
    val code: Long,
    val message: String
)
