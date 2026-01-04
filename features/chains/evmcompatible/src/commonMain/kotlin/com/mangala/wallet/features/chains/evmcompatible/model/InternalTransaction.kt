package com.mangala.wallet.features.chains.evmcompatible.model

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import kotlin.js.JsExport

//@Entity
data class InternalTransaction(
    val hash: ByteArray,
    val blockNumber: Long,
    val from: Address,
    val to: Address,
    val value: BigInteger,
//    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
) {

//    @delegate:JsExport.Ignore
    val hashString: String by lazy {
        hash.toHexString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is InternalTransaction)
            return false

        return hash.contentEquals(other.hash) && id == other.id
    }

    override fun hashCode(): Int {
        return hash.hashCode() + id.hashCode()
    }

}
