package com.mangala.wallet.domain.transaction.fee

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.utils.ext.toBigDecimal
import kotlin.math.min

data class TransactionFeeOption(
    val gasPrice: BigDecimal, // Legacy gas
    val baseFee: BigDecimal?, // EIP-1559
    val priorityFee: BigDecimal?, // EIP-1559
    val transactionFeeType: TransactionFeeType
) {
    val maxGas: BigDecimal
        get() {
            return if (baseFee != null && priorityFee != null) {
                min(
                    gasPrice.longValue(false),
                    baseFee.longValue(false) + priorityFee.longValue(false)
                )
            } else {
                gasPrice.longValue(false)
            }.toBigDecimal()
        }
}
