package com.mangala.wallet.uniswap.domain.models

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.removeTrailingZeroes

class TradeData(
    val trade: Trade,
    val options: TradeOptions
) {

    val tokenAmountInMax: TokenAmount
        get() {
            val amountInMax = ((Fraction(BigInteger.ONE) + options.slippageFraction) * Fraction(trade.tokenAmountIn.rawAmount)).quotient
            return TokenAmount(trade.tokenAmountIn.token, amountInMax)
        }

    val tokenAmountOutMin: TokenAmount
        get() {
            val amountOutMin = ((Fraction(BigInteger.ONE) + options.slippageFraction).invert() * Fraction(trade.tokenAmountOut.rawAmount)).quotient
            return TokenAmount(trade.tokenAmountOut.token, amountOutMin)
        }

    val type = trade.type

    val amountIn: BigDecimal? = trade.tokenAmountIn.decimalAmount?.removeTrailingZeroes()

    val amountOut: BigDecimal? = trade.tokenAmountOut.decimalAmount?.removeTrailingZeroes()

    val amountInMax: BigDecimal? = tokenAmountInMax.decimalAmount?.removeTrailingZeroes()

    val amountOutMin: BigDecimal? = tokenAmountOutMin.decimalAmount?.removeTrailingZeroes()

    val executionPrice: BigDecimal? = trade.executionPrice.decimalValue?.removeTrailingZeroes()

    val midPrice: BigDecimal? = trade.route.midPrice.decimalValue?.removeTrailingZeroes()

    val priceImpact: BigDecimal? = trade.priceImpact.toBigDecimal(2)

    val providerFee: BigDecimal?
        get() {
            val amount = (if (type == TradeType.ExactIn) amountIn else amountInMax) ?: return null

            return trade.liquidityProviderFee.toBigDecimal(trade.tokenAmountIn.token.decimals)?.let {
                it * amount
            }
        }

    val path: List<Token> = trade.route.path
}
