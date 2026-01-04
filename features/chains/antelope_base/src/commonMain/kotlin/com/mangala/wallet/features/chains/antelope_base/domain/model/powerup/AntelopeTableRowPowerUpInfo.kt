package com.mangala.wallet.features.chains.antelope_base.domain.model.powerup

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.antelope_balance.Balance

data class AntelopeTableRowPowerUpInfo(
    val cpu: Resource,
    val net: Resource,
    val powerUpDays: Int,
    val minPowerUpFee: Balance
) {
    data class Resource(
        val weight: BigDecimal,
        val utilization: BigDecimal,
        val utilizationTimestamp: Long, // UNIX timestamp in seconds format
        val adjustedUtilization: BigDecimal,
        val decaySecs: Long,
        val minPrice: Balance,
        val maxPrice: Balance,
        val exponent: BigDecimal
    )
}