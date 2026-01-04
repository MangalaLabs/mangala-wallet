package com.mangala.wallet.binance.data.model

import com.ionspin.kotlin.bignum.integer.BigInteger

data class Transaction(
    val nonce: BigInteger,
    val gasPrice: BigInteger,
    val gasLimit: BigInteger,
    val recipient: String,
    val value: BigInteger,
    val data: ByteArray
)
