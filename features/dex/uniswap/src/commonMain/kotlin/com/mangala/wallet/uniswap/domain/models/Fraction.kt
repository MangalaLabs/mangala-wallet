package com.mangala.wallet.uniswap.domain.models

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.getScale

class Fraction : Comparable<Fraction> {
    val numerator: BigInteger
    val denominator: BigInteger

    constructor(numerator: BigInteger, denominator: BigInteger = BigInteger.ONE) {
        this.numerator = numerator
        this.denominator = denominator
    }

    constructor(decimal: BigDecimal) {
        val scale = decimal.getScale()
//        decimal.moveDecimalPoint(scale)
        numerator = decimal.moveDecimalPoint(scale).toBigInteger()
        denominator = BigInteger.TEN.pow(scale.toBigInteger())
    }

    val quotient: BigInteger
        get() = numerator / denominator

    val remainder: Fraction
        get() = Fraction(numerator % denominator, denominator)

    fun invert(): Fraction = Fraction(denominator, numerator)

    fun toBigDecimal(decimals: Int): BigDecimal? {
        return try {

            val result = BigDecimal.fromBigInteger(numerator).divide(BigDecimal.fromBigInteger(denominator), DecimalMode(decimals.toLong(), RoundingMode.ROUND_HALF_CEILING))

            return result
        } catch (ex: ArithmeticException) {
            ex.printStackTrace()
            null
        }
    }

    override fun toString(): String {
        return "$numerator / $denominator"
    }

    override fun compareTo(other: Fraction): Int {
        val thisAdjustedNumerator = this.numerator * other.denominator
        val otherAdjustedNumerator = other.numerator * this.denominator

        return when {
            thisAdjustedNumerator == otherAdjustedNumerator -> 0
            thisAdjustedNumerator > otherAdjustedNumerator -> 1
            else -> -1 // thisAdjustedNumerator < otherAdjustedNumerator
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other is Fraction) {
            this.compareTo(other) == 0
        } else false
    }

    operator fun plus(other: Fraction) = if (this.denominator == other.denominator) {
        Fraction(this.numerator + other.numerator, this.denominator)
    } else
        Fraction(this.numerator * other.denominator + other.numerator * this.denominator, this.denominator * other.denominator)

    operator fun minus(other: Fraction) = if (this.denominator == other.denominator) {
        Fraction(this.numerator - other.numerator, this.denominator)
    } else
        Fraction(this.numerator * other.denominator - other.numerator * this.denominator, this.denominator * other.denominator)

    operator fun times(other: Fraction) = Fraction(this.numerator * other.numerator, this.denominator * other.denominator)

    operator fun div(other: Fraction) = Fraction(this.numerator * other.denominator, this.denominator * other.numerator)

}
