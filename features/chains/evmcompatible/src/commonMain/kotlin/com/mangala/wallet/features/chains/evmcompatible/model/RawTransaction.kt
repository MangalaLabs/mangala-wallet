package com.mangala.wallet.features.chains.evmcompatible.model

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString

class RawTransaction(
    val gasPrice: GasPrice,
    val gasLimit: Long,
    val to: Address,
    val value: BigInteger,
    val nonce: Long,
    val data: ByteArray = ByteArray(0),
    val isDeploySmartContract: Boolean = false
) {

    override fun toString(): String {
        return "RawTransaction [gasPrice: $gasPrice; gasLimit: $gasLimit; to: $to; value: $value; data: ${data.toHexString()}; nonce: $nonce]"
    }
}
