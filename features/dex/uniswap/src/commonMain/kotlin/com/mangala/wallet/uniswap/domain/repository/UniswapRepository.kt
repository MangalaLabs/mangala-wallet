package com.mangala.wallet.uniswap.domain.repository

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.SwapData
import com.mangala.wallet.uniswap.domain.models.Token
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.uniswap.domain.models.TradeOptions
import com.mangala.wallet.uniswap.domain.models.TradeType

interface UniswapRepository {
    suspend fun getSwapData(
        blockchainType: BlockchainType,
        id: Int,
        address: Address,
        dex: Dex,
        tokenFrom: Token,
        tokenTo: Token,
        forceRefresh: Boolean = false
    ): SwapData

    suspend fun getTradeData(
        blockchainType: BlockchainType,
        id: Int,
        address: Address,
        dex: Dex,
        swapData: SwapData,
        amount: BigDecimal,
        tradeType: TradeType,
        tradeOptions: TradeOptions
    ): TradeData

    fun getTransactionData(
        blockchainType: BlockchainType,
        id: Int,
        address: Address,
        dex: Dex,
        tradeData: TradeData
    ): TransactionData
}