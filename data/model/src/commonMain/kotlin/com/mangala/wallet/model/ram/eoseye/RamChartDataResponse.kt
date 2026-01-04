package com.mangala.wallet.model.ram.eoseye

import kotlinx.serialization.Serializable

@Serializable
data class RamChartDataResponse(
    val data: List<RamChartData>,
    val code: Int,
    val message: String
)
@Serializable
data class RamChartData(
    val date: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)