package com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexfund

import com.mangala.wallet.features.chains.antelopebase.AntelopeRexFundEntity
import kotlinx.coroutines.flow.Flow

internal interface AntelopeRexFundLocalDataSource {
    suspend fun getRexFund(accountName: String, blockchainUid: String): AntelopeRexFundEntity?
    fun getRexFundFlow(accountName: String, blockchainUid: String): Flow<AntelopeRexFundEntity?>
    suspend fun insertRexFund(rexFundEntity: AntelopeRexFundEntity)
}