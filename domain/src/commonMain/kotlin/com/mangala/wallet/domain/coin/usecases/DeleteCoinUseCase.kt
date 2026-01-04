package com.mangala.wallet.domain.coin.usecases

import com.mangala.wallet.domain.blockchain.repository.BlockchainRepository
import com.mangala.wallet.domain.coin.repository.CoinRepository

class DeleteCoinUseCase(private val coinRepository: CoinRepository) {
    suspend operator fun invoke() {
        coinRepository.clearDatabase()
    }
}