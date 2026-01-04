package com.mangala.wallet.utils.ext

import kotlinx.datetime.LocalDateTime

fun LocalDateTime.formatToHourMinute(): String {
    return "${this.hour.toString().padStart(2, '0')}:${this.minute.toString().padStart(2, '0')}"
}