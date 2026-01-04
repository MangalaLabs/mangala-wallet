package com.mangala.wallet.domain.token.price.usecases

import com.mangala.wallet.domain.token.price.repository.TokenPriceRepository
import com.mangala.wallet.model.token.TokenPriceEntity

class GetTokenPriceByCoinIdUseCase(private val tokenPriceRepository: TokenPriceRepository) {

    suspend operator fun invoke(coinId: String): TokenPriceEntity? {
        return tokenPriceRepository.getTokenPriceByCoinId(coinId).firstOrNull()
    }

    suspend fun getByCurrencyCode(coinId: String, currencyCode: String) {
        tokenPriceRepository.getTokenPriceByCoinIdAndCurrencyCode(coinId, currencyCode)
    }

}