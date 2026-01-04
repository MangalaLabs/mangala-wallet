package com.mangala.wallet.domain.token.usecases

import com.mangala.wallet.domain.coin.repository.CoinRepository
import com.mangala.wallet.domain.token.repository.TokenRepository

class DeleteTokenByIdUseCase(private val tokenRepository: TokenRepository) {
    suspend operator fun invoke(id: Long){
        tokenRepository.deleteTokenById(id)
    }
}