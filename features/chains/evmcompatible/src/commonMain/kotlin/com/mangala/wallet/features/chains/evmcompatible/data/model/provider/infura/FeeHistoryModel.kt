package com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura

import com.mangala.wallet.model.Dto
import com.mangala.wallet.model.Model

data class FeeHistoryModel(
    val baseFeePerGas: List<Long>,
    val gasUsedRatio: List<Double>,
    val oldestBlock: Long,
    val reward: List<List<Long>>
) : Model {
    override fun toLocalDto(): Dto {
        TODO("Not yet implemented")
    }

    override fun toRemoteDto(): Dto {
        TODO("Not yet implemented")
    }

}