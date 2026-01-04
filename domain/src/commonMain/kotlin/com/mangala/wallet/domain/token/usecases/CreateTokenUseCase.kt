package com.mangala.wallet.domain.token.usecases

import com.mangala.wallet.domain.token.repository.TokenRepository
import com.mangala.wallet.model.token.TokenEntity

class CreateTokenUseCase(private val tokenRepository: TokenRepository) {

    suspend operator fun invoke(tokens: List<TokenEntity>) {
        return tokenRepository.insertToken(tokens)
    }

}