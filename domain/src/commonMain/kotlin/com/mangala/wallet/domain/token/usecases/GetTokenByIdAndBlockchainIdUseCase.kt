package com.mangala.wallet.domain.token.usecases

import com.mangala.wallet.domain.token.repository.TokenRepository
import com.mangala.wallet.model.token.TokenEntity
import com.mangala.wallet.model.token.TokenType.Native.id

class GetTokenByIdAndBlockchainIdUseCase(private val tokenRepository: TokenRepository) {
    suspend operator fun invoke(uid: String, blockchainId: String): List<TokenEntity>{
        return tokenRepository.getTokenByCoinUidAndBlockchainUid(uid, blockchainId)
    }
}