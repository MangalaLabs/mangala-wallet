package com.mangala.wallet.domain.token.balance.usecases

import com.mangala.wallet.domain.token.repository.TokenRepository
import com.mangala.wallet.model.token.TokenBalanceEntity

class GetTokenBalanceByTokenIdUseCase(private val tokenBalanceRepository: TokenRepository) {

    operator fun invoke(tokenId: Long, accountId: String): List<TokenBalanceEntity>{
        return tokenBalanceRepository.getTokenBalanceByTokenIdAndAccountId(tokenId, accountId)
    }

    suspend fun byAccountId(accountId: String): List<TokenBalanceEntity>{
        return tokenBalanceRepository.getTokenBalanceByAccountId(accountId)
    }
}