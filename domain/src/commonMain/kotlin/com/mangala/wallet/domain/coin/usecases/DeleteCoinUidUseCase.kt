package com.mangala.wallet.domain.coin.usecases

import com.mangala.wallet.domain.coin.repository.CoinRepository

class DeleteCoinUidUseCase(private val coinRepository: CoinRepository) {
    suspend operator fun invoke(uid: String) {
        coinRepository.deleteCoinById(uid)
    }
}