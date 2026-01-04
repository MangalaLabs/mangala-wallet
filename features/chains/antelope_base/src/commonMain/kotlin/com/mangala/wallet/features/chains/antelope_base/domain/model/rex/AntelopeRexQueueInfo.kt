package com.mangala.wallet.features.chains.antelope_base.domain.model.rex

import kotlinx.datetime.Instant

data class AntelopeRexQueueInfo(
    val rows: List<Row>
) {
    data class Row(
        val orderTime: Instant,
        val owner: String,
        val proceeds: String,
        val rexRequested: String,
        val stakeChange: String,
    )
}