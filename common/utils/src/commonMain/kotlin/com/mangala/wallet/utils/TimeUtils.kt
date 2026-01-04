package com.mangala.wallet.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun currentTimeInMillis() = Clock.System.now().toEpochMilliseconds()

fun convertUtcMillisToLocal(
    epochMillis: Long,
    localTimeHour: Int = 0,
    localTimeMinute: Int = 0,
    localTimeSecond: Int = 0,
    localTimeNanoSeconds: Int = 0
): LocalDateTime {
    val utcInstant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = utcInstant.toLocalDateTime(TimeZone.UTC)

    return LocalDateTime(localDateTime.date, LocalTime(localTimeHour, localTimeMinute, localTimeSecond, localTimeNanoSeconds))
}

fun Long?.secondsTimestampToMillisecondTimestamp() = this?.times(1000) ?: 0L