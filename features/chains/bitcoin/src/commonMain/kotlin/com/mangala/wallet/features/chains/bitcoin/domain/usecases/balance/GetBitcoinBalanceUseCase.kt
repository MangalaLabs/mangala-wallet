package com.mangala.wallet.features.chains.bitcoin.domain.usecases.balance

import com.mangala.wallet.features.chains.bitcoin.domain.repository.balance.BitcoinBalanceRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class GetBitcoinBalanceUseCase(
    private val bitcoinBalanceRepository: BitcoinBalanceRepository
) {
    suspend operator fun invoke(
        forceRefresh: Boolean,
        accountId: String,
        address: String,
        blockchainType: BlockchainType
    ) = bitcoinBalanceRepository.getBalance(
        forceRefresh,
        accountId,
        address,
        blockchainType
    )
}