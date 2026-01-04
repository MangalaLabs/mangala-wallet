package com.mangala.wallet.features.chains.evmcompatible.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.utils.ext.toBigDecimal
import kotlinx.serialization.Serializable
import kotlin.math.min

sealed class GasPrice {
    data class Legacy(val legacyGasPrice: Long) : GasPrice()
    data class Eip1559(val maxFeePerGas: Long, val maxPriorityFeePerGas: Long, val baseFee: Long) : GasPrice()

    val max: Long
        get() = when (this) {
            is Eip1559 -> maxFeePerGas
            is Legacy -> legacyGasPrice
        }

    val maxGas: Long
        get() = when(this) {
            is Legacy -> max
            is Eip1559 -> min(
                maxFeePerGas,
                baseFee + maxPriorityFeePerGas
            )
        }

    override fun toString() = when (this) {
        is Eip1559 -> "Eip1559 [maxFeePerGas: $maxFeePerGas, maxPriorityFeePerGas: $maxPriorityFeePerGas]"
        is Legacy -> "Legacy [gasPrice: $legacyGasPrice]"
    }
}
