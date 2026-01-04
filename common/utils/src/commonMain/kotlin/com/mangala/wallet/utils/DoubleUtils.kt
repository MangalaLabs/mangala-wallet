package com.mangala.wallet.utils

import kotlin.math.pow
import kotlin.math.round

fun Double.round(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return round(this * factor) / factor
}

/**
 * Extension function để chuyển đổi Double từ độ sang radian
 */
fun Double.toRadians(): Double = this * kotlin.math.PI / 180.0