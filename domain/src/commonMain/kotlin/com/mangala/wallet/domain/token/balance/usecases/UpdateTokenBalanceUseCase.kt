package com.mangala.wallet.domain.token.balance.usecases

import com.mangala.wallet.domain.token.repository.TokenRepository
import com.mangala.wallet.model.token.TokenBalanceEntity

class UpdateTokenBalanceUseCase(private val tokenBalanceRepository: TokenRepository) {

    suspend operator fun invoke(tokenBalances: List<TokenBalanceEntity>){
        return tokenBalanceRepository.updateTokenBalance(tokenBalances)
    }

}