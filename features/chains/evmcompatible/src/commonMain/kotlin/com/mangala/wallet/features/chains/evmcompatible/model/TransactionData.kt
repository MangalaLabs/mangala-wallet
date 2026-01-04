package com.mangala.wallet.features.chains.evmcompatible.model

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import kotlinx.serialization.Serializable

data class TransactionData(
    val to: Address?,
    val value: BigInteger,
    val input: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other is TransactionData -> to == other.to && value == other.value && input.contentEquals(other.input)
            else -> false
        }
    }

    override fun hashCode(): Int {
        return to?.hashCode() ?: 0 + value.hashCode() + input.hashCode()
    }

    override fun toString(): String {
        return "TransactionData {to: ${to?.hex}, value: $value, input: ${input.toHexString()}}"
    }

}
