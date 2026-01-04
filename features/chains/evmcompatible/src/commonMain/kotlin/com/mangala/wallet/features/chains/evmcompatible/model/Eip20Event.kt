package com.mangala.wallet.features.chains.evmcompatible.model

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import kotlin.js.JsExport

//@Entity
class Eip20Event(
    val hash: ByteArray,
    val blockNumber: Long,
    val contractAddress: Address,
    val from: Address,
    val to: Address,
    val value: BigInteger,

    val tokenName: String,
    val tokenSymbol: String,
    val tokenDecimal: Int,

//    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
) {

//    @delegate:JsExport.Ignore
    val hashString: String by lazy {
        hash.toHexString()
    }

}
