package com.mangala.wallet.features.chains.antelope_base.data.local.account.token

import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountTokenBalanceEntity
import kotlinx.coroutines.flow.Flow

internal interface AntelopeAccountTokenBalanceLocalDataSource {
    suspend fun getAccountTokenBalance(accountName: String, blockchainUid: String): List<AntelopeAccountTokenBalanceEntity>
    fun getAccountTokenBalanceFlow(
        accountName: String,
        blockchainUid: String
    ): Flow<List<AntelopeAccountTokenBalanceEntity>>
    suspend fun insertAccountTokenBalance(tokenBalanceData: List<AntelopeAccountTokenBalanceEntity>)
    suspend fun deleteAccountTokenBalanceByAccount(accountName: String, blockchainUid: String)
}