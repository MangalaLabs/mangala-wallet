package com.mangala.wallet.features.chains.antelope_base.data.repository.ram

import com.mangala.antelope.base.api.model.BaseGetTableRowsResponse
import com.mangala.antelope.base.api.model.RamMarketRowResponse
import com.mangala.antelope.base.api.model.RexFundRowResponse
import com.mangala.antelope.base.model.RamMarketData
import com.mangala.wallet.features.chains.antelope_base.domain.model.ram.AntelopeRamMarketInfo
import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexFundInfo
import com.mangala.wallet.features.chains.antelopebase.AntelopeRamMarketEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeRexFundEntity
import kotlinx.datetime.Clock

fun BaseGetTableRowsResponse<RamMarketRowResponse>.toAntelopeRamMarketEntity(
    blockchainUid: String,
): AntelopeRamMarketEntity? {
    val lastUpdatedTimestamp = Clock.System.now().toEpochMilliseconds()

    return rows?.firstOrNull()?.toAntelopeRamMarketEntity(blockchainUid, lastUpdatedTimestamp)
}

fun RamMarketRowResponse.toAntelopeRamMarketEntity(
    blockchainUid: String,
    lastUpdatedTimestamp: Long
): AntelopeRamMarketEntity {
    return AntelopeRamMarketEntity(
        blockchain_uid = blockchainUid,
        last_updated = lastUpdatedTimestamp,
        quote_weight = quote?.weight.orEmpty(),
        base_weight = base?.weight.orEmpty(),
        quote_balance = quote?.balance.orEmpty(),
        base_balance = base?.balance.orEmpty(),
        supply = supply.orEmpty()
    )
}

fun AntelopeRamMarketEntity.toAntelopeRamMarketInfo(): AntelopeRamMarketInfo {
    return AntelopeRamMarketInfo(
        supply = supply,
        base = AntelopeRamMarketInfo.Pair(
            balance = base_balance,
            weight = base_weight.toDouble()
        ),
        quote = AntelopeRamMarketInfo.Pair(
            balance = quote_balance,
            weight = quote_weight.toDouble()
        )
    )
}