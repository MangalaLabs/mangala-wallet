package com.mangala.wallet.features.chains.antelope_base.data.repository.rex.rexfund

import com.benasher44.uuid.uuid4
import com.mangala.antelope.base.api.model.BaseGetTableRowsResponse
import com.mangala.antelope.base.api.model.RexFundRowResponse
import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexFundInfo
import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexQueueInfo
import com.mangala.wallet.features.chains.antelopebase.AntelopeRexFundEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeRexQueueEntity
import com.mangala.wallet.remote.di.ApiResponse.Error.SerializationError.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun BaseGetTableRowsResponse<RexFundRowResponse>.toAntelopeRexFundEntity(
    accountName: String,
    blockchainUid: String,
): AntelopeRexFundEntity? {
    val lastUpdatedTimestamp = Clock.System.now().toEpochMilliseconds()

    return rows?.firstOrNull()?.toAntelopeRexFundEntity(accountName, blockchainUid, lastUpdatedTimestamp)
}

fun RexFundRowResponse.toAntelopeRexFundEntity(
    accountName: String,
    blockchainUid: String,
    lastUpdatedTimestamp: Long
): AntelopeRexFundEntity {
    return AntelopeRexFundEntity(
        account_name = accountName,
        blockchain_uid = blockchainUid,
        balance = balance.orEmpty(),
        last_updated = lastUpdatedTimestamp
    )
}

fun AntelopeRexFundEntity.toAntelopeRexFund(): AntelopeRexFundInfo {
    return AntelopeRexFundInfo(
        balance = balance,
        owner = account_name
    )
}