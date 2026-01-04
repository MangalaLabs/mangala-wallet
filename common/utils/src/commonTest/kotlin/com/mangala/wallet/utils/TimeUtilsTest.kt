package com.mangala.wallet.utils

import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeUtilsTest {

    @Test
    fun `Given UTC epoch_when convertUtcMillisToLocal called_then return local 0_00 time`() {
        val epochMillis = 1694563200L * 1000 // Wed Sep 13 2023 00:00:00 GMT+0000
        val result = convertUtcMillisToLocal(epochMillis)

        assertEquals(13, result.dayOfMonth)
        assertEquals(9, result.monthNumber)
        assertEquals(2023, result.year)
        assertEquals(0, result.hour)
        assertEquals(0, result.minute)
    }

    @Test
    fun `Given UTC epoch near day boundary_when convertUtcMillisToLocal called_then return correct local 0_00 time`() {
        val epochMillis = 1694627940L * 1000 // Wed Sep 13 2023 17:59:00 GMT+0000
        val result = convertUtcMillisToLocal(epochMillis)

        assertEquals(13, result.dayOfMonth)
        assertEquals(9, result.monthNumber)
        assertEquals(2023, result.year)
        assertEquals(0, result.hour)
        assertEquals(0, result.minute)
    }
}