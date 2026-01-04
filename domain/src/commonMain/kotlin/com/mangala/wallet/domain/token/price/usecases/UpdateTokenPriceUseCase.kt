package com.mangala.wallet.domain.token.price.usecases

import com.mangala.wallet.domain.token.price.repository.TokenPriceRepository
import com.mangala.wallet.model.token.TokenPriceEntity

class UpdateTokenPriceUseCase(private val tokenPriceRepository: TokenPriceRepository) {

    suspend operator fun invoke(tokenPrices: List<TokenPriceEntity>){
        return tokenPriceRepository.updateTokenPrice(tokenPrices)
    }

}
