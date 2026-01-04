package com.mangala.wallet.uniswap.domain.usecase

import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.uniswap.domain.repository.UniswapRepository
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.TradeData

class GetSwapTransactionDataUseCase(
    private val uniswapRepository: UniswapRepository
) {
    operator fun invoke(blockchainType: BlockchainType, id: Int, address: Address, dex: Dex, tradeData: TradeData) =
        uniswapRepository.getTransactionData(blockchainType, id, address, dex, tradeData)
}