package com.mangala.wallet.domain.token.usecases

import com.mangala.wallet.domain.token.repository.TokenRepository
import com.mangala.wallet.model.token.TokenEntity

class GetTokenByIdUseCase(private val tokenRepository: TokenRepository) {
    suspend operator fun invoke(id: Long): List<TokenEntity>{
        return tokenRepository.getTokenById(id)
    }
}