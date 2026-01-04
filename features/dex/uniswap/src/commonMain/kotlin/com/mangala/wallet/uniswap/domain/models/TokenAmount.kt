package com.mangala.wallet.uniswap.domain.models

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.removeTrailingZeroes
import com.mangala.wallet.uniswap.TokenAmountError
import com.mangala.wallet.utils.ext.ethToWei

class TokenAmount : Comparable<TokenAmount> {
    val token: Token
    val amount: Fraction

    constructor(token: Token, rawAmount: BigInteger) {
        check(rawAmount.signum() >= 0) {
            throw TokenAmountError.NegativeAmount()
        }
        this.token = token
        this.amount = Fraction(rawAmount, BigInteger.TEN.pow(token.decimals))
    }

    constructor(token: Token, decimal: BigDecimal) : this(token, getRawAmount(token, decimal))

    constructor(token: Token, amount: Fraction) {
        this.token = token
        this.amount = amount
    }

    val rawAmount: BigInteger
        get() = amount.numerator

    val decimalAmount: BigDecimal?
        get() = amount.toBigDecimal(token.decimals)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other is TokenAmount) {
            this.compareTo(other) == 0
        } else false
    }

    override fun compareTo(other: TokenAmount): Int {
        check(this.token == other.token)

        return this.amount.compareTo(other.amount)
    }

    override fun toString(): String {
        return "{$token: ${decimalAmount?.removeTrailingZeroes()}}"
    }

    companion object {
        private fun getRawAmount(token: Token, decimal: BigDecimal): BigInteger {
            return decimal.ethToWei(token.decimals).toBigInteger()
        }
    }

}
