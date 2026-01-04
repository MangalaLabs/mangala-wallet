package com.mangala.wallet.common.test.utils

import kotlin.time.Duration
import kotlin.time.measureTime

fun <T> callAndMeasureTiming(call: () -> T): Pair<T, Duration> {
    var value: T

    val duration = measureTime {
        value = call()
    }

    return value to duration
}