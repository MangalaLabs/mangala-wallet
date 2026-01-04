package com.mangala.wallet.domain.token.usecases

import com.mangala.wallet.domain.token.repository.TokenRepository
import com.mangala.wallet.model.token.TokenEntity

class GetTokenByReferenceUseCase(private val tokenRepository: TokenRepository) {
    suspend operator fun invoke(reference: String): List<TokenEntity>{
        return tokenRepository.getTokenByReference(reference)
    }

    suspend operator fun invoke(reference: String, blockchainUid: String): List<TokenEntity>{
        return tokenRepository.getTokenByReference(reference, blockchainUid)
    }
}