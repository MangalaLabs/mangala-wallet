package com.mangala.wallet.uniswap.domain.models

import com.ionspin.kotlin.bignum.integer.BigInteger

class Trade(
    val type: TradeType,
    val route: Route,
    val tokenAmountIn: TokenAmount,
    val tokenAmountOut: TokenAmount
) : Comparable<Trade> {
    val executionPrice = Price(baseTokenAmount = tokenAmountIn, quoteTokenAmount = tokenAmountOut)
    val priceImpact = computePriceImpact(route.midPrice, tokenAmountIn, tokenAmountOut)
    val liquidityProviderFee = computeLiquidityProviderFee(route.pairs.size)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other is Trade) {
            this.compareTo(other) == 0
        } else false
    }

    override fun compareTo(other: Trade): Int {
        if (this.tokenAmountOut != other.tokenAmountOut) {
            //reverse order to make bigger amount first
            return other.tokenAmountOut.compareTo(this.tokenAmountOut)
        }

        if (this.tokenAmountIn != other.tokenAmountIn) {
            return this.tokenAmountIn.compareTo(other.tokenAmountIn)
        }

        if (this.priceImpact != other.priceImpact) {
            return this.priceImpact.compareTo(other.priceImpact)
        }

        return this.route.path.size - other.route.path.size
    }

    companion object {
        private fun computePriceImpact(midPrice: Price, tokenAmountIn: TokenAmount, tokenAmountOut: TokenAmount): Fraction {
            val exactQuote = midPrice.value * Fraction(tokenAmountIn.rawAmount) * Fraction(
                BigInteger.fromInt(997), BigInteger.fromInt(1000))
            return (exactQuote - Fraction(tokenAmountOut.rawAmount)) / exactQuote * Fraction(BigInteger.fromInt(100))
        }

        private fun computeLiquidityProviderFee(routeLength: Int): Fraction {
            return Fraction(BigInteger.ONE) - Fraction(BigInteger.fromInt(997).pow(routeLength), BigInteger.fromInt(1000).pow(routeLength))
        }
    }
}
