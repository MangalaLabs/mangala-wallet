/*
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mangala.antelope.base.core.utilities

//import com.mangala.wallet.utils.formatTime
import kotlinx.datetime.*

object DateFormatter {

    private val BACKEND_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    private val BACKEND_DATE_PATTERN_WITH_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    private val BACKEND_DATE_TIME_ZONE = "UTC"

    /**
     * Converting backend time to millisecond.
     *
     * @param backendTime input backend time.
     * @return Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by parsed input backend time.
     * @throws IllegalArgumentException thrown if the input does not match with any supported datetime pattern.
     */
    fun convertBackendTimeToMilli(backendTime: String): Long {
        val datePatterns = listOf(
            BACKEND_DATE_PATTERN, BACKEND_DATE_PATTERN_WITH_TIMEZONE
        )

        for (pattern in datePatterns) {
            try {
                val instant = Instant.parse(backendTime)
                return instant.toEpochMilliseconds()
            } catch (e: Exception) {
                // Continue to try the next pattern
            }
        }

        throw IllegalArgumentException("Unable to parse input backend time with supported date patterns!")
    }

    /**
     * Convert milliseconds to time string format used on blockchain.
     *
     * @param timeInMilliSeconds input number of milliseconds
     * @return String format of input number of milliseconds
     */
    fun convertMilliSecondToBackendTimeString(timeInMilliSeconds: Long): String {
        val instant = Instant.fromEpochMilliseconds(timeInMilliSeconds)
        val dateTime = instant.toLocalDateTime(TimeZone.of(BACKEND_DATE_TIME_ZONE))

        return buildString {
            append(dateTime.year.toString().padStart(4, '0'))
            append('-')
            append(dateTime.monthNumber.toString().padStart(2, '0'))
            append('-')
            append(dateTime.dayOfMonth.toString().padStart(2, '0'))
            append('T')
            append(dateTime.hour.toString().padStart(2, '0'))
            append(':')
            append(dateTime.minute.toString().padStart(2, '0'))
            append(':')
            append(dateTime.second.toString().padStart(2, '0'))
            append('.')
            append(dateTime.nanosecond.toString().padStart(3, '0').substring(0, 3))
        }

//        return dateTime.toString(BACKEND_DATE_PATTERN)
    }
}