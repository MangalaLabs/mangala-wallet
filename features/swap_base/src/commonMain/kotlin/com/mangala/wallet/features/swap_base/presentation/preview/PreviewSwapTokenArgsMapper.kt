package com.mangala.wallet.features.swap_base.presentation.preview

import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.ui.utils.navigation.args.PreviewSwapTokenScreenArgs
import com.mangala.wallet.ui.utils.navigation.args.Token
import com.mangala.wallet.uniswap.domain.models.Fraction
import com.mangala.wallet.uniswap.domain.models.Pair
import com.mangala.wallet.uniswap.domain.models.Route
import com.mangala.wallet.uniswap.domain.models.TokenAmount
import com.mangala.wallet.uniswap.domain.models.Trade
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.uniswap.domain.models.TradeOptions
import com.mangala.wallet.uniswap.domain.models.TradeType

fun PreviewSwapTokenScreenArgs.getTradeData(): TradeData {
    return TradeData(
        trade = Trade(
            type = TradeType.valueOf(type),
            route = route.toRoute(),
            tokenAmountIn = tokenAmountIn.toTokenAmount(),
            tokenAmountOut = tokenAmountOut.toTokenAmount()
        ),
        options = TradeOptions(
            allowedSlippagePercent,
            ttl,
            recipient?.let { Address(it) },
            feeOnTransfer
        )
    )
}

fun com.mangala.wallet.ui.utils.navigation.args.Route.toRoute(): Route {
    return Route(
        pairs = pairs.map { pair ->
            pair.toPair()
        },
        tokenIn = tokenIn.toToken(),
        tokenOut = tokenOut.toToken()
    )
}

fun com.mangala.wallet.ui.utils.navigation.args.Pair.toPair(): Pair {
    return Pair(
        reserve0 = reserve0.toTokenAmount(),
        reserve1 = reserve1.toTokenAmount()
    )
}

fun com.mangala.wallet.ui.utils.navigation.args.TokenAmount.toTokenAmount(): TokenAmount {
    return TokenAmount(
        token = token.toToken(),
        amount = amount.toFraction()
    )
}

fun Token.toToken(): com.mangala.wallet.uniswap.domain.models.Token {
    return when(this) {
        is Token.Ether -> com.mangala.wallet.uniswap.domain.models.Token.Ether(Address(this.address))
        is Token.Erc20 -> com.mangala.wallet.uniswap.domain.models.Token.Erc20(Address(this.address), this.decimals)
    }
}

fun com.mangala.wallet.ui.utils.navigation.args.Fraction.toFraction(): Fraction {
    return Fraction(numerator, denominator)
}