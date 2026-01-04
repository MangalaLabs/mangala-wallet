package com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura

import com.mangala.wallet.features.chains.evmcompatible.core.hexStringToLongOrNull
import com.mangala.wallet.model.Dto

@kotlinx.serialization.Serializable
data class FeeHistoryDto(
    val id: Int?,
    val jsonrpc: String?,
    val result: Result?
){
    @kotlinx.serialization.Serializable
    data class Result(
        val baseFeePerGas: List<String?>?,
        val gasUsedRatio: List<Double?>?,
        val oldestBlock: String?,
        val reward: List<List<String?>>?
    ):Dto{
        override fun mapToDomainModel(): FeeHistoryModel {
            return FeeHistoryModel(
                baseFeePerGas?.map {
                    it?.hexStringToLongOrNull() ?: 0L
                } ?: emptyList(),
                gasUsedRatio?.map {
                    it ?: 0.0
                } ?: emptyList(),
                oldestBlock?.hexStringToLongOrNull() ?: 0L,
                reward?.map {
                    it?.map {
                        it?.hexStringToLongOrNull() ?: 0L
                    } ?: emptyList()
                } ?: emptyList()
            )
        }

    }
}