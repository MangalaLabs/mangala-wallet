package com.mangala.wallet.uniswap.data.repository

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.uniswap.data.services.UniswapService
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.SwapData
import com.mangala.wallet.uniswap.domain.models.Token
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.uniswap.domain.models.TradeOptions
import com.mangala.wallet.uniswap.domain.models.TradeType
import com.mangala.wallet.uniswap.domain.repository.UniswapRepository

class UniswapRepositoryImpl(
    private val uniswapService: UniswapService
): UniswapRepository {

    private lateinit var swapDataCache: Pair<String, SwapData>

    override suspend fun getSwapData(
        blockchainType: BlockchainType,
        id: Int,
        address: Address,
        dex: Dex,
        tokenFrom: Token,
        tokenTo: Token,
        forceRefresh: Boolean
    ): SwapData {
        val cacheKey = getCacheKey(tokenFrom, tokenTo)
        return if (forceRefresh || !::swapDataCache.isInitialized || swapDataCache.first != cacheKey) {
            swapDataCache = cacheKey to uniswapService.getSwapData(blockchainType, id, address, dex, tokenFrom, tokenTo)
            swapDataCache.second
        } else {
            swapDataCache.second
        }
    }

    override suspend fun getTradeData(
        blockchainType: BlockchainType,
        id: Int,
        address: Address,
        dex: Dex,
        swapData: SwapData,
        amount: BigDecimal,
        tradeType: TradeType,
        tradeOptions: TradeOptions
    ): TradeData {
        return uniswapService.getTradeData(blockchainType, id, address, dex, swapData, amount, tradeType, tradeOptions)
    }

    override fun getTransactionData(
        blockchainType: BlockchainType,
        id: Int,
        address: Address,
        dex: Dex,
        tradeData: TradeData
    ): TransactionData {
        return uniswapService.getTransactionData(blockchainType, id, address, dex, tradeData)
    }

    private fun getCacheKey(tokenFrom: Token, tokenTo: Token): String {
        return tokenFrom.hashCode().toString() + tokenTo.hashCode().toString()
    }
}