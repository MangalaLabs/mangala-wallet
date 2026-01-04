package com.mangala.wallet.domain.coin.usecases

import com.mangala.wallet.domain.coin.repository.CoinRepository
import com.mangala.wallet.model.blockchain.BlockchainEntity
import com.mangala.wallet.model.coin.Coin

class GetAllCoinUseCase(private val coinRepository: CoinRepository) {

    suspend operator fun invoke(): List<Coin> {
        return coinRepository.getAllCoin()
    }

}