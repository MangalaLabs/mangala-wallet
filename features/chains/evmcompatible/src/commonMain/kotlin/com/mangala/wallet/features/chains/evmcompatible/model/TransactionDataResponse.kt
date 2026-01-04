package com.mangala.wallet.features.chains.evmcompatible.model

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString

@kotlinx.serialization.Serializable
data class TransactionDataResponse(
    val result: String?,
    val jsonrpc: String?,
    val id: Long?
)
