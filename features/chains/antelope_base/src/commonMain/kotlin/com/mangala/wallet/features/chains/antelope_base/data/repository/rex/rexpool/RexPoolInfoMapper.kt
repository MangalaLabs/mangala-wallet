package com.mangala.wallet.features.chains.antelope_base.data.repository.rex.rexpool

import com.mangala.antelope.base.api.model.BaseGetTableRowsResponse
import com.mangala.antelope.base.api.model.RexPoolRowResponse
import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexPoolInfo
import com.mangala.wallet.features.chains.antelopebase.AntelopeRexPoolEntity
import kotlinx.datetime.Clock

fun BaseGetTableRowsResponse<RexPoolRowResponse>.toAntelopeRexPoolEntity(
    blockchainUid: String,
): AntelopeRexPoolEntity? {
    val lastUpdatedTimestamp = Clock.System.now().toEpochMilliseconds()

    return rows?.firstOrNull()?.toAntelopeRexPoolEntity(blockchainUid, lastUpdatedTimestamp)
}

fun RexPoolRowResponse.toAntelopeRexPoolEntity(
    blockchainUid: String,
    lastUpdatedTimestamp: Long
): AntelopeRexPoolEntity {
    return AntelopeRexPoolEntity(
        blockchain_uid = blockchainUid,
        last_updated = lastUpdatedTimestamp,
        total_lent = totalLent.orEmpty(),
        total_unlent = totalUnlent.orEmpty(),
        total_rent = totalRent.orEmpty(),
        total_lendable = totalLendable.orEmpty(),
        total_rex = totalRex.orEmpty(),
        namebid_proceeds = namebidProceeds.orEmpty(),
        loan_num = loanNum?.toLong() ?: 0,
    )
}

fun AntelopeRexPoolEntity.toAntelopeRexPool(): AntelopeRexPoolInfo {
    return AntelopeRexPoolInfo(
        totalLent = total_lent,
        totalUnlent = total_unlent,
        totalRent = total_rent,
        totalLendable = total_lendable,
        totalRex = total_rex,
        nameBidProceeds = namebid_proceeds,
        loanNum = loan_num.toInt(),
    )
}