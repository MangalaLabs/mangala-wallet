package com.mangala.wallet.uniswap.domain.models

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.getScale
import com.mangala.wallet.features.chains.evmcompatible.core.removeTrailingZeroes
import com.mangala.wallet.features.chains.evmcompatible.model.Address


class TradeOptions(
    var allowedSlippagePercent: BigDecimal = defaultAllowedSlippage,
    var ttl: Long = defaultTtl,
    var recipient: Address? = null,
    var feeOnTransfer: Boolean = false
) {

    val slippageFraction: Fraction
        get() = try {
            val strippedSlippage = allowedSlippagePercent.removeTrailingZeroes()
            val scaledSlippage = strippedSlippage.scale(strippedSlippage.getScale() + 2)
            Fraction(scaledSlippage / BigDecimal.fromInt(100))
        } catch (error: Exception) {
            Fraction(BigInteger.fromInt(5), BigInteger.fromInt(1000))
        }

    companion object {
        val defaultAllowedSlippage = BigDecimal.parseString("0.5")
        val defaultTtl: Long = 20 * 60
    }
}
