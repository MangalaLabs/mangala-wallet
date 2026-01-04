package com.mangala.wallet.features.chains.antelope_base.data.repository.rex.rexqueue

import com.benasher44.uuid.uuid4
import com.mangala.antelope.base.api.model.BaseGetTableRowsResponse
import com.mangala.antelope.base.api.model.RexQueueRowResponse
import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexQueueInfo
import com.mangala.wallet.features.chains.antelopebase.AntelopeRexQueueEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun BaseGetTableRowsResponse<RexQueueRowResponse>.toAntelopeRexQueueEntity(
    accountName: String,
    blockchainUid: String,
): List<AntelopeRexQueueEntity> {
    val lastUpdatedTimestamp = Clock.System.now().toEpochMilliseconds()

    return rows?.map {
        it.toAntelopeRexQueueEntity(accountName, blockchainUid, lastUpdatedTimestamp)
    } ?: emptyList()
}

fun RexQueueRowResponse.toAntelopeRexQueueEntity(
    accountName: String,
    blockchainUid: String,
    lastUpdatedTimestamp: Long
): AntelopeRexQueueEntity {
    return AntelopeRexQueueEntity(
        id = uuid4().toString(),
        account_name = accountName,
        blockchain_uid = blockchainUid,
        order_time = Instant.parse("${orderTime}Z").toEpochMilliseconds(),
        proceeds = proceeds.orEmpty(),
        rex_requested = rexRequested.orEmpty(),
        stake_change = stakeChange.orEmpty(),
        last_updated = lastUpdatedTimestamp
    )
}

fun List<AntelopeRexQueueEntity>.toAntelopeRexQueueInfo(): AntelopeRexQueueInfo {
    return AntelopeRexQueueInfo(
        rows = map {
            AntelopeRexQueueInfo.Row(
                orderTime = Instant.fromEpochMilliseconds(it.order_time),
                owner = it.account_name,
                proceeds = it.proceeds,
                rexRequested = it.rex_requested,
                stakeChange = it.stake_change,
            )
        }
    )
}