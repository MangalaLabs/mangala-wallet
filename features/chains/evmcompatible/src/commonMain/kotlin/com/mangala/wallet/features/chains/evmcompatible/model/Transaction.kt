package com.mangala.wallet.features.chains.evmcompatible.model

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import kotlin.js.JsExport

//@Entity
class Transaction(
//    @PrimaryKey
    val hash: ByteArray,
    val timestamp: Long,
    var isFailed: Boolean,

    val blockNumber: Long? = null,
    val transactionIndex: Int? = null,
    val from: Address? = null,
    val to: Address? = null,
    val value: BigInteger? = null,
    val input: ByteArray? = null,
    val nonce: Long? = null,
    val gasPrice: Long? = null,
    val maxFeePerGas: Long? = null,
    val maxPriorityFeePerGas: Long? = null,
    val gasLimit: Long? = null,
    val gasUsed: Long? = null,

    var replacedWith: ByteArray? = null
) {

//    @delegate:JsExport.Ignore
    val hashString: String by lazy {
        hash.toHexString()
    }

}
