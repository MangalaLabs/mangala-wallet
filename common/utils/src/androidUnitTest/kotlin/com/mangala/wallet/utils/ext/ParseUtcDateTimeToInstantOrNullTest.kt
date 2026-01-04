package com.mangala.wallet.utils.ext

import org.junit.Assert.assertEquals
import org.junit.Test

class ParseUtcDateTimeToInstantOrNullTest {

    @Test
    fun `Given a string in the standard ISO format, when calling parseUtcDateTimeToInstantOrNull, then it should return the correct Instant`() {
        val dateTime = "2021-09-01T00:00:00.000Z"

        val result = dateTime.parseUtcDateTimeToInstantOrNull()

        assertEquals(1630454400000, result?.toEpochMilliseconds())
    }

    @Test
    fun `Given a UTC string without trailing Z, when calling parseUtcDateTimeToInstantOrNull, then it should return the correct Instant`() {
        val dateTime = "2021-09-01T00:00:00.000"

        val result = dateTime.parseUtcDateTimeToInstantOrNull()

        assertEquals(1630454400000, result?.toEpochMilliseconds())
    }

    @Test
    fun `Given a UTC string without milliseconds component but with trailing Z, when calling parseUtcDateTimeToInstantOrNull, then it should return the correct Instant`() {
        val dateTime = "2021-09-01T00:00:00Z"

        val result = dateTime.parseUtcDateTimeToInstantOrNull()

        assertEquals(1630454400000, result?.toEpochMilliseconds())
    }

    @Test
    fun `Given a UTC string without milliseconds component and trailing Z, when calling parseUtcDateTimeToInstantOrNull, then it should return the correct Instant`() {
        val dateTime = "2021-09-01T00:00:00"

        val result = dateTime.parseUtcDateTimeToInstantOrNull()

        assertEquals(1630454400000, result?.toEpochMilliseconds())
    }

    @Test
    fun `Given a UTC string without seconds component and trailing Z, when calling parseUtcDateTimeToInstantOrNull, then it should return the correct Instant`() {
        val dateTime = "2021-09-01T00:00"

        val result = dateTime.parseUtcDateTimeToInstantOrNull()

        assertEquals(1630454400000, result?.toEpochMilliseconds())
    }
}