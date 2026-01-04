package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.features.chains.antelope_base.domain.usecase.token.GetAntelopeAccountTokenBalanceUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class RefreshAccountBalanceUseCase(
    private val getAccountWithBalanceInfoUseCase: GetAccountWithBalanceInfoUseCase,
    private val getAntelopeAccountTokenBalanceUseCase: GetAntelopeAccountTokenBalanceUseCase
) {
    suspend operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType,
        refreshTokenBalance: Boolean
    ) {
        coroutineScope {
            val coreBalanceAsync = async {
                getAccountWithBalanceInfoUseCase(accountName, blockchainType, forceRefresh = true)
            }

            val tokenBalanceAsync = if (refreshTokenBalance) {
                async {
                    getAntelopeAccountTokenBalanceUseCase(
                        accountName,
                        blockchainType,
                        forceRefresh = true
                    )
                }
            } else null

            coreBalanceAsync.await()
            tokenBalanceAsync?.await()
        }
    }
}