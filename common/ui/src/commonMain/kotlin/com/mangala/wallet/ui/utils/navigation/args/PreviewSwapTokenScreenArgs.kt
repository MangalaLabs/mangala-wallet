package com.mangala.wallet.ui.utils.navigation.args

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger

data class PreviewSwapTokenScreenArgs(
    val accountAddress: String,
    val accountName: String,
    val accountId: String,
    val tokenFromSymbol: String,
    val tokenFromLogoUrl: String,
    val tokenToSymbol: String,
    val tokenToLogoUrl: String,
    val type: String,
    val route: Route,
    val tokenAmountIn: TokenAmount,
    val tokenAmountOut: TokenAmount,
    val allowedSlippagePercent: BigDecimal,
    val ttl: Long,
    val recipient: String?,
    val feeOnTransfer: Boolean,
    val dex: String
)

data class Fraction(
    val numerator: BigInteger,
    val denominator: BigInteger
)

data class Pair(
    val reserve0: TokenAmount,
    val reserve1: TokenAmount
)

sealed class Token(
    val address: String,
    val decimals: Int,
    val isEther: Boolean = false
) {

    class Ether(val wethAddress: String) : Token(wethAddress, 18, true)
    class Erc20(address: String, decimals: Int) : Token(address, decimals)
}

class TokenAmount(
    val token: Token,
    val amount: Fraction
)

class Route(
    val pairs: List<Pair>,
    val tokenIn: Token,
    val tokenOut: Token
)
