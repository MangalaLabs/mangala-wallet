package com.mangala.wallet.domain.token.usecases

import app.cash.paging.PagingData
import com.mangala.wallet.domain.token.repository.TokenRepository
import com.mangala.wallet.model.token.TokenEntity
import kotlinx.coroutines.flow.Flow

class GetTokenByBlockchainUidUseCase(private val tokenRepository: TokenRepository) {
    suspend fun getFirst2Token(blockchainUid: String): List<TokenEntity> {
        return tokenRepository.getFirst2TokenByBlockchainUid(blockchainUid)
    }

    fun invokePaging(blockchainUid: String): Flow<PagingData<TokenEntity>> {
        return tokenRepository.getPaginatedTokenByBlockchainUid(blockchainUid)
    }
}