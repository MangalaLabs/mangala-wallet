package com.mangala.wallet.domain.coin.usecases

import com.mangala.wallet.domain.coin.repository.CoinRepository
import com.mangala.wallet.model.coin.Coin

class CreateCoinUseCase(private val coinRepository: CoinRepository) {

    suspend operator fun invoke(coins: List<Coin>) {
        return coinRepository.insertCoin(coins)
    }

}