package com.mangala.wallet.utils

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.measureTime

suspend fun <T> executeWithMinDelay(minDelay: Duration, action: suspend () -> T): T {
    var result: T

    val timeTaken = measureTime {
        result = action()
    }

    if (timeTaken < minDelay) {
        delay(minDelay - timeTaken)
    }

    return result
}