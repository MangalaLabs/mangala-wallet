package com.mangala.wallet.features.chains.antelope_base.data.repository.powerup

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.api.model.BaseGetTableRowsResponse
import com.mangala.antelope.base.api.model.PowerupState
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.model.powerup.AntelopeTableRowPowerUpInfo
import com.mangala.wallet.features.chains.antelopebase.AntelopeTableRowsPowerUpEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun BaseGetTableRowsResponse<PowerupState>.toAntelopeTableRowsPowerUpEntity(
    blockchainUid: String,
): AntelopeTableRowsPowerUpEntity? {
    val lastUpdatedTimestamp = Clock.System.now().toEpochMilliseconds()

    return rows?.firstOrNull()
        ?.toAntelopeTableRowsPowerUpEntity(blockchainUid, lastUpdatedTimestamp)
}

fun PowerupState.toAntelopeTableRowsPowerUpEntity(
    blockchainUid: String, lastUpdatedTimestamp: Long
): AntelopeTableRowsPowerUpEntity {
    return AntelopeTableRowsPowerUpEntity(
        blockchain_uid = blockchainUid,
        last_updated = lastUpdatedTimestamp,
        cpu_utilization = cpu?.utilization ?: "",
        cpu_weight = cpu?.weight ?: "",
        net_utilization = net?.utilization ?: "",
        net_weight = net?.weight ?: "",
        cpu_adjusted_utilization = cpu?.adjustedUtilization ?: "",
        net_adjusted_utilization = net?.adjustedUtilization ?: "",
        cpu_decay_secs = cpu?.decaySecs ?: 0L,
        net_decay_secs = net?.decaySecs ?: 0L,
        cpu_min_price = cpu?.minPrice ?: "",
        net_min_price = net?.minPrice ?: "",
        cpu_max_price = cpu?.maxPrice ?: "",
        net_max_price = net?.maxPrice ?: "",
        cpu_exponent = cpu?.exponent ?: "",
        net_exponent = net?.exponent ?: "",
        cpu_utilization_timestamp = cpu?.utilizationTimestamp ?: "",
        net_utilization_timestamp = net?.utilizationTimestamp ?: "",
        powerup_days = powerupDays?.toLong() ?: 0,
        min_powerup_fee = minPowerupFee ?: ""
    )
}

fun AntelopeTableRowsPowerUpEntity.toAntelopeTableRowsPowerUpInfo(): AntelopeTableRowPowerUpInfo {
    return AntelopeTableRowPowerUpInfo(
        cpu = AntelopeTableRowPowerUpInfo.Resource(
            weight = cpu_weight.toBigDecimal(),
            utilization = cpu_utilization.toBigDecimal(),
            adjustedUtilization = cpu_adjusted_utilization.toBigDecimal(),
            decaySecs = cpu_decay_secs,
            minPrice = BalanceFormatter.deserialize(cpu_min_price),
            maxPrice = BalanceFormatter.deserialize(cpu_max_price),
            exponent = cpu_exponent.toBigDecimal(),
            utilizationTimestamp = Instant.parse("${cpu_utilization_timestamp}Z").epochSeconds
        ),
        net = AntelopeTableRowPowerUpInfo.Resource(
            weight = net_weight.toBigDecimal(),
            utilization = net_utilization.toBigDecimal(),
            adjustedUtilization = net_adjusted_utilization.toBigDecimal(),
            decaySecs = net_decay_secs,
            minPrice = BalanceFormatter.deserialize(net_min_price),
            maxPrice = BalanceFormatter.deserialize(net_max_price),
            exponent = net_exponent.toBigDecimal(),
            utilizationTimestamp = Instant.parse("${net_utilization_timestamp}Z").epochSeconds
        ),
        powerUpDays = powerup_days.toInt(),
        minPowerUpFee = BalanceFormatter.deserialize(min_powerup_fee)
    )
}
