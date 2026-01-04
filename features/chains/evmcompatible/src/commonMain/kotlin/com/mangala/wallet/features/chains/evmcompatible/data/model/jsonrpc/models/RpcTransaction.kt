package com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.model.Address

class RpcTransaction(
    val hash: ByteArray,
    val nonce: Long,
    val blockHash: ByteArray?,
    val blockNumber: Long?,
    val transactionIndex: Int?,
    val from: Address,
    val to: Address?,
    val value: BigInteger,
    val gasPrice: Long,
    val maxFeePerGas: Long?,
    val maxPriorityFeePerGas: Long?,
    val gas: Long, //gasLimit
    val input: ByteArray
)
