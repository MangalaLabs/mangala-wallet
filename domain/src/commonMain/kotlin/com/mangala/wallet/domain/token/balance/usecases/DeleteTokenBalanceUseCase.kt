package com.mangala.wallet.domain.token.balance.usecases

import com.mangala.wallet.domain.token.repository.TokenRepository

class DeleteTokenBalanceUseCase(private val tokenBalanceRepository: TokenRepository) {

    suspend operator fun invoke(tokenId: Long, accountId: String){
        return tokenBalanceRepository.deleteTokenBalanceByTokenIdAndAccountId(tokenId, accountId)
    }

}