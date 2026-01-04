package com.mangala.wallet.uniswap

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.core.toBigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.CallNodeUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.uniswap.contract.GetReservesMethod
import com.mangala.wallet.uniswap.contract.SwapETHForExactTokensMethod
import com.mangala.wallet.uniswap.contract.SwapExactETHForTokensMethod
import com.mangala.wallet.uniswap.contract.SwapExactTokensForETHMethod
import com.mangala.wallet.uniswap.contract.SwapExactTokensForTokensMethod
import com.mangala.wallet.uniswap.contract.SwapTokensForExactETHMethod
import com.mangala.wallet.uniswap.contract.SwapTokensForExactTokensMethod
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.Route
import com.mangala.wallet.uniswap.domain.models.Token
import com.mangala.wallet.uniswap.domain.models.TokenAmount
import com.mangala.wallet.uniswap.domain.models.Trade
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.uniswap.domain.models.TradeType
import com.mangala.wallet.utils.DebugLog
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TradeManager(val url: String, val id: Int, val address: Address, chain: Chain, dex: Dex): KoinComponent {

    private val callNodeUseCase: CallNodeUseCase by inject()

    val routerAddress: Address = getRouterAddress(chain, dex)
    val factoryAddressString: String = getFactoryAddressString(chain, dex)
    val initCodeHashString: String = getInitCodeHashString(chain, dex)

    sealed class UnsupportedChainError : Throwable() {
        object NoRouterAddress : UnsupportedChainError()
        object NoFactoryAddress : UnsupportedChainError()
        object NoInitCodeHash : UnsupportedChainError()
    }

    suspend fun pair(tokenA: Token, tokenB: Token): com.mangala.wallet.uniswap.domain.models.Pair? {

        val (token0, token1) = if (tokenA.sortsBefore(tokenB)) Pair(tokenA, tokenB) else Pair(tokenB, tokenA)

        val pairAddress = com.mangala.wallet.uniswap.domain.models.Pair.address(token0, token1, factoryAddressString, initCodeHashString)



        val data =  callNodeUseCase.invoke(url, id, pairAddress, GetReservesMethod().encodedABI(), DefaultBlockParameter.Latest)

        data?.let {


            var rawReserve0: BigInteger = BigInteger.ZERO
            var rawReserve1: BigInteger = BigInteger.ZERO

            if (data.size == 3 * 32) {
                rawReserve0 = data.copyOfRange(0, 32).toBigInteger()
                rawReserve1 = data.copyOfRange(32, 64).toBigInteger()

            }

            val reserve0 = TokenAmount(token0, rawReserve0)
            val reserve1 = TokenAmount(token1, rawReserve1)

            return com.mangala.wallet.uniswap.domain.models.Pair(reserve0, reserve1)
        }
        return null
    }

    fun transactionData(tradeData: TradeData): TransactionData {
        return buildSwapData(tradeData).let {
            TransactionData(routerAddress, it.amount, it.input)
        }
    }

    private class SwapData(val amount: BigInteger, val input: ByteArray)

    private fun buildSwapData(tradeData: TradeData): SwapData {
        val trade = tradeData.trade

        val tokenIn = trade.tokenAmountIn.token
        val tokenOut = trade.tokenAmountOut.token

        val path = trade.route.path.map { it.address }
        val to = tradeData.options.recipient ?: address
        val deadline = (Clock.System.now().toEpochMilliseconds() / 1000 + tradeData.options.ttl).toBigInteger()

        val method = when (trade.type) {
            TradeType.ExactOut -> buildMethodForExactOut(tokenIn, tokenOut, path, to, deadline, tradeData, trade)
            TradeType.ExactIn -> buildMethodForExactIn(tokenIn, tokenOut, path, to, deadline, tradeData, trade)
        }

        val amount = if (tokenIn.isEther) {
            when (trade.type) {
                TradeType.ExactIn -> trade.tokenAmountIn.rawAmount
                TradeType.ExactOut -> tradeData.tokenAmountInMax.rawAmount
            }
        } else {
            BigInteger.ZERO
        }

        return SwapData(amount, method.encodedABI())
    }

    private fun buildMethodForExactOut(
        tokenIn: Token,
        tokenOut: Token,
        path: List<Address>,
        to: Address,
        deadline: BigInteger,
        tradeData: TradeData,
        trade: Trade
    ): ContractMethod {
        val amountInMax = tradeData.tokenAmountInMax.rawAmount
        val amountOut = trade.tokenAmountOut.rawAmount

        return when {
            tokenIn is Token.Ether && tokenOut is Token.Erc20 -> SwapETHForExactTokensMethod(amountOut, path, to, deadline)
            tokenIn is Token.Erc20 && tokenOut is Token.Ether -> SwapTokensForExactETHMethod(amountOut, amountInMax, path, to, deadline)
            tokenIn is Token.Erc20 && tokenOut is Token.Erc20 -> SwapTokensForExactTokensMethod(amountOut, amountInMax, path, to, deadline)
            else -> throw Exception("Invalid tokenIn/Out for swap!")
        }
    }

    private fun buildMethodForExactIn(
        tokenIn: Token,
        tokenOut: Token,
        path: List<Address>,
        to: Address,
        deadline: BigInteger,
        tradeData: TradeData,
        trade: Trade
    ): ContractMethod {
        val amountIn = trade.tokenAmountIn.rawAmount
        val amountOutMin = tradeData.tokenAmountOutMin.rawAmount

        return when {
            tokenIn is Token.Ether && tokenOut is Token.Erc20 -> SwapExactETHForTokensMethod(amountOutMin, path, to, deadline)
            tokenIn is Token.Erc20 && tokenOut is Token.Ether -> SwapExactTokensForETHMethod(amountIn, amountOutMin, path, to, deadline)
            tokenIn is Token.Erc20 && tokenOut is Token.Erc20 -> SwapExactTokensForTokensMethod(amountIn, amountOutMin, path, to, deadline)
            else -> throw Exception("Invalid tokenIn/Out for swap!")
        }
    }

    companion object {

        private fun getRouterAddress(chain: Chain, dex: Dex) =
            when (chain) {
                Chain.Ethereum -> {
                    val addressString = dex.addresses.find { it.first == Chain.Ethereum }?.second?.routerAddress.orEmpty()
                    Address(addressString)
                }

                Chain.EthereumGoerli -> {
                    val addressString = dex.addresses.find { it.first == Chain.EthereumGoerli }?.second?.routerAddress.orEmpty()
                    Address(addressString)
                }

                Chain.EthereumSepolia -> {
                    val addressString = dex.addresses.find { it.first == Chain.EthereumSepolia }?.second?.routerAddress.orEmpty()
                    Address(addressString)
                }

                Chain.EthereumHolesky -> {
                    val addressString = dex.addresses.find { it.first == Chain.EthereumHolesky }?.second?.routerAddress.orEmpty()
                    Address(addressString)
                }

                Chain.BinanceSmartChain -> {
                    val addressString = dex.addresses.find { it.first == Chain.BinanceSmartChain }?.second?.routerAddress.orEmpty()
                    Address(addressString)
                }

                Chain.BinanceSmartChainTestNet -> {
                    val addressString = dex.addresses.find { it.first == Chain.BinanceSmartChainTestNet }?.second?.routerAddress.orEmpty()
                    Address(addressString)
                }

                Chain.Polygon -> Address("0xa5E0829CaCEd8fFDD4De3c43696c57F7D7A678ff")
                Chain.Avalanche -> Address("0x60aE616a2155Ee3d9A68541Ba4544862310933d4")
                else -> throw UnsupportedChainError.NoRouterAddress
            }

        private fun getFactoryAddressString(chain: Chain, dex: Dex) =
            when (chain) {
                Chain.Ethereum -> {
                    val addressString = dex.addresses.find { it.first == Chain.Ethereum }?.second?.factoryAddress.orEmpty()
                    addressString
                }

                Chain.EthereumGoerli -> {
                    val addressString = dex.addresses.find { it.first == Chain.EthereumGoerli }?.second?.factoryAddress.orEmpty()
                    addressString
                }

                Chain.EthereumSepolia -> {
                    val addressString = dex.addresses.find { it.first == Chain.EthereumSepolia }?.second?.factoryAddress.orEmpty()
                    addressString
                }

                Chain.EthereumHolesky -> {
                    val addressString = dex.addresses.find { it.first == Chain.EthereumHolesky }?.second?.factoryAddress.orEmpty()
                    addressString
                }

                Chain.BinanceSmartChain -> {
                    val addressString = dex.addresses.find { it.first == Chain.BinanceSmartChain }?.second?.factoryAddress.orEmpty()
                    addressString
                }

                Chain.BinanceSmartChainTestNet -> {
                    val addressString = dex.addresses.find { it.first == Chain.BinanceSmartChainTestNet }?.second?.factoryAddress.orEmpty()
                    addressString
                }

                Chain.Polygon -> "0x5757371414417b8C6CAad45bAeF941aBc7d3Ab32"
                Chain.Avalanche -> "0x9Ad6C38BE94206cA50bb0d90783181662f0Cfa10"
                else -> throw UnsupportedChainError.NoFactoryAddress
            }

        private fun getInitCodeHashString(chain: Chain, dex: Dex) =
            when (chain) {
                Chain.Polygon, Chain.Avalanche -> "0x96e8ac4277198ff8b6f785478aa9a39f403cb768dd02cbee326c3e7da348845f"
                Chain.Ethereum -> {
                    val addressString = dex.addresses.find { it.first == Chain.Ethereum }?.second?.initCodeHash.orEmpty()
                    addressString
                }

                Chain.EthereumGoerli -> {
                    val addressString = dex.addresses.find { it.first == Chain.EthereumGoerli }?.second?.initCodeHash.orEmpty()
                    addressString
                }

                Chain.EthereumSepolia -> {
                    val addressString = dex.addresses.find { it.first == Chain.EthereumSepolia }?.second?.initCodeHash.orEmpty()
                    addressString
                }

                Chain.EthereumHolesky -> {
                    val addressString = dex.addresses.find { it.first == Chain.EthereumHolesky }?.second?.initCodeHash.orEmpty()
                    addressString
                }

                Chain.BinanceSmartChain -> dex.addresses.find { it.first == Chain.BinanceSmartChain }?.second?.initCodeHash.orEmpty()
                Chain.BinanceSmartChainTestNet -> dex.addresses.find { it.first == Chain.BinanceSmartChainTestNet }?.second?.initCodeHash.orEmpty()
                else -> throw UnsupportedChainError.NoInitCodeHash
            }

        fun tradeExactIn(
            pairs: List<com.mangala.wallet.uniswap.domain.models.Pair>,
            tokenAmountIn: TokenAmount,
            tokenOut: Token,
            maxHops: Int = 3,
            currentPairs: List<com.mangala.wallet.uniswap.domain.models.Pair> = listOf(),
            originalTokenAmountIn: TokenAmount? = null
        ): List<Trade> {
            //todo validations

            val trades = mutableListOf<Trade>()
            val originalTokenAmountIn = originalTokenAmountIn ?: tokenAmountIn

            for ((index, pair) in pairs.withIndex()) {

                val tokenAmountOut = try {
                    pair.tokenAmountOut(tokenAmountIn)
                } catch (error: Throwable) {
                    continue
                }

                if (tokenAmountOut.token == tokenOut) {
                    val trade = Trade(
                        type = TradeType.ExactIn,
                        route = Route(currentPairs + listOf(pair), originalTokenAmountIn.token, tokenOut),
                        tokenAmountIn = originalTokenAmountIn,
                        tokenAmountOut = tokenAmountOut
                    )
                    trades.add(trade)
                } else if (maxHops > 1 && pairs.size > 1) {
                    val pairsExcludingThisPair = pairs.toMutableList().apply { removeAt(index) }
                    val tradesRecursion = tradeExactIn(
                        pairs = pairsExcludingThisPair,
                        tokenAmountIn = tokenAmountOut,
                        tokenOut = tokenOut,
                        maxHops = maxHops - 1,
                        currentPairs = currentPairs + listOf(pair),
                        originalTokenAmountIn = originalTokenAmountIn
                    )
                    trades.addAll(tradesRecursion)
                }
            }
            return trades
        }

        fun tradeExactOut(
            pairs: List<com.mangala.wallet.uniswap.domain.models.Pair>,
            tokenIn: Token,
            tokenAmountOut: TokenAmount,
            maxHops: Int = 3,
            currentPairs: List<com.mangala.wallet.uniswap.domain.models.Pair> = listOf(),
            originalTokenAmountOut: TokenAmount? = null
        ): List<Trade> {
            //todo validations

            val trades = mutableListOf<Trade>()
            val originalTokenAmountOut = originalTokenAmountOut ?: tokenAmountOut

            for ((index, pair) in pairs.withIndex()) {

                val tokenAmountIn = try {
                    pair.tokenAmountIn(tokenAmountOut)
                } catch (error: Throwable) {
                    continue
                }

                if (tokenAmountIn.token == tokenIn) {
                    val trade = Trade(
                        type = TradeType.ExactOut,
                        route = Route(listOf(pair) + currentPairs, tokenIn, originalTokenAmountOut.token),
                        tokenAmountIn = tokenAmountIn,
                        tokenAmountOut = originalTokenAmountOut
                    )
                    trades.add(trade)
                } else if (maxHops > 1 && pairs.size > 1) {
                    val pairsExcludingThisPair = pairs.toMutableList().apply { removeAt(index) }
                    val tradesRecursion = tradeExactOut(
                        pairs = pairsExcludingThisPair,
                        tokenIn = tokenIn,
                        tokenAmountOut = tokenAmountIn,
                        maxHops = maxHops - 1,
                        currentPairs = currentPairs + listOf(pair),
                        originalTokenAmountOut = originalTokenAmountOut
                    )
                    trades.addAll(tradesRecursion)
                }
            }
            return trades
        }

    }

}
