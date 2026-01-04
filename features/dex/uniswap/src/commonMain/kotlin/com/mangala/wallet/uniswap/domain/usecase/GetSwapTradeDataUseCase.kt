package com.mangala.wallet.uniswap.domain.usecase

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.uniswap.domain.repository.UniswapRepository
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.Token
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.uniswap.domain.models.TradeOptions
import com.mangala.wallet.uniswap.domain.models.TradeType

class GetSwapTradeDataUseCase(
    private val uniswapRepository: UniswapRepository // TODO: Handle 1Inch swaps as well
) {
    // Gets trade data containing info including amount for the other token, swap fees, slippage, ...
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        id: Int,
        address: Address,
        dex: Dex,
        tradeType: TradeType,
        tokenFrom: Token,
        tokenTo: Token,
        amount: BigDecimal?,
        tradeOptions: TradeOptions,
        forceRefresh: Boolean = false
    ): TradeData? {
        val swapData = uniswapRepository.getSwapData(
            blockchainType,
            id,
            address,
            dex,
            tokenFrom,
            tokenTo,
            forceRefresh
        )

        if (amount == null) return null

        return uniswapRepository.getTradeData(
            blockchainType,
            id,
            address,
            dex,
            swapData,
            amount,
            tradeType,
            tradeOptions
        ) // TODO: Handle trade options
    }
}