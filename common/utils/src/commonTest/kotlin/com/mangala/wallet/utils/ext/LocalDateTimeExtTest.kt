package com.mangala.wallet.utils.ext

import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalDateTimeExtTest {

    @Test
    fun `Given two-digit hour and minute_when formatToHourMinute called_then return formatted time`() {
        val localDateTime = LocalDateTime(2023, 8, 19, 15, 30)
        val expected = "15:30"
        val result = localDateTime.formatToHourMinute()
        assertEquals(expected, result)
    }

    @Test
    fun `Given one-digit hour and minute_when formatToHourMinute called_then return formatted time`() {
        val localDateTime = LocalDateTime(2023, 8, 19, 5, 7)
        val expected = "05:07"
        val result = localDateTime.formatToHourMinute()
        assertEquals(expected, result)
    }
}