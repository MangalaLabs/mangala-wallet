package com.mangala.wallet.uniswap

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.SwapData
import com.mangala.wallet.uniswap.domain.models.Token
import com.mangala.wallet.uniswap.domain.models.TokenAmount
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.uniswap.domain.models.TradeOptions
import com.mangala.wallet.utils.DebugLog

class Uniswap(
    private val tradeManager: TradeManager,
    private val pairSelector: PairSelector,
    private val tokenFactory: TokenFactory
) {
//    private val logger = Logger.getLogger(this.javaClass.simpleName)

    val routerAddress: Address
        get() = tradeManager.routerAddress

    fun etherToken(): Token {
        return tokenFactory.etherToken()
    }

    fun token(contractAddress: Address, decimals: Int): Token {
        return tokenFactory.token(contractAddress, decimals)
    }

    suspend fun swapData(tokenIn: Token, tokenOut: Token): SwapData {
        val tokenPairs = pairSelector.tokenPairs(tokenIn, tokenOut)
        val singles = tokenPairs.map { (tokenA, tokenB) ->
            tradeManager.pair(tokenA, tokenB)
        }
        val pairs = singles.filterNotNull()
        return SwapData(pairs, tokenIn, tokenOut)
    }

    fun bestTradeExactIn(swapData: SwapData, amountIn: BigDecimal, options: TradeOptions = TradeOptions()): TradeData {
        val tokenAmountIn = TokenAmount(swapData.tokenIn, amountIn)
        val sortedTrades = TradeManager.tradeExactIn(
            swapData.pairs,
            tokenAmountIn,
            swapData.tokenOut
        ).sorted()


        sortedTrades.forEachIndexed { index, trade ->
//            DebugLog.log("$index: {in: ${trade.tokenAmountIn}, out: ${trade.tokenAmountOut}, impact: ${trade.priceImpact.toBigDecimal(2)}, pathSize: ${trade.route.path.size}")
        }

        val trade = sortedTrades.firstOrNull() ?: throw TradeError.TradeNotFound()


        return TradeData(trade, options)
    }

    fun bestTradeExactOut(swapData: SwapData, amountOut: BigDecimal, options: TradeOptions = TradeOptions()): TradeData {
        val tokenAmountOut = TokenAmount(swapData.tokenOut, amountOut)
        val sortedTrades = TradeManager.tradeExactOut(
            swapData.pairs,
            swapData.tokenIn,
            tokenAmountOut
        ).sorted()


        sortedTrades.forEachIndexed { index, trade ->
//            DebugLog.log("$index: {in: ${trade.tokenAmountIn}, out: ${trade.tokenAmountOut}, impact: ${trade.priceImpact}, pathSize: ${trade.route.path.size}")
        }

        val trade = sortedTrades.firstOrNull() ?: throw TradeError.TradeNotFound()

        return TradeData(trade, options)
    }

    fun transactionData(tradeData: TradeData): TransactionData {
        return tradeManager.transactionData(tradeData)
    }

    companion object {
        fun getInstance(url: String, id: Int, address: Address, chain: Chain, dex: Dex): Uniswap {
            val tradeManager = TradeManager(url, id, address, chain, dex)
            val tokenFactory = TokenFactory(chain)
            val pairSelector = PairSelector(tokenFactory)

            return Uniswap(tradeManager, pairSelector, tokenFactory)
        }

//        fun addDecorators(ethereumKit: EthereumKit) {
//            ethereumKit.addMethodDecorator(SwapMethodDecorator(SwapContractMethodFactories))
//            ethereumKit.addTransactionDecorator(SwapTransactionDecorator())
//        }

    }
}