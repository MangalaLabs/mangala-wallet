package com.mangala.wallet.domain.token.price.usecases

import com.mangala.wallet.domain.token.price.repository.TokenPriceRepository

class DeleteTokenPriceUseCase(private val tokenPriceRepository: TokenPriceRepository) {

    suspend operator fun invoke(coinId: String){
        tokenPriceRepository.deleteTokenPriceByCoinUid(coinId)
    }

}