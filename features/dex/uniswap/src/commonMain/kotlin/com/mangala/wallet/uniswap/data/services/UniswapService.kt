package com.mangala.wallet.uniswap.data.services

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.uniswap.Uniswap
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.SwapData
import com.mangala.wallet.uniswap.domain.models.Token
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.uniswap.domain.models.TradeOptions
import com.mangala.wallet.uniswap.domain.models.TradeType


class UniswapService {
    suspend fun getSwapData(blockchainType: BlockchainType, id: Int, address: Address, dex: Dex, tokenFrom: Token, tokenTo: Token): SwapData {
        val uniswapKit = getUniswapKit(blockchainType, id, address, dex)

        return uniswapKit.swapData(tokenFrom, tokenTo)
    }

    fun getTradeData(blockchainType: BlockchainType, id: Int, address: Address, dex: Dex, swapData: SwapData, amount: BigDecimal, tradeType: TradeType, tradeOptions: TradeOptions): TradeData {
        val uniswapKit = getUniswapKit(blockchainType, id, address, dex)

        return when (tradeType) {
            TradeType.ExactIn -> {
                uniswapKit.bestTradeExactIn(swapData, amount, tradeOptions)
            }
            TradeType.ExactOut -> {
                uniswapKit.bestTradeExactOut(swapData, amount, tradeOptions)
            }
        }
    }

    fun getTransactionData(blockchainType: BlockchainType, id: Int, address: Address, dex: Dex, tradeData: TradeData): TransactionData {
        val uniswapKit = getUniswapKit(blockchainType, id, address, dex)

        return uniswapKit.transactionData(tradeData)
    }

    private fun getUniswapKit(blockchainType: BlockchainType, id: Int, address: Address, dex: Dex): Uniswap {
        val url = blockchainType.getRpcUrl().first()
        val chain = Chain.fromBlockchainType(blockchainType)
        return Uniswap.getInstance(url, id, address, chain, dex)
    }
}