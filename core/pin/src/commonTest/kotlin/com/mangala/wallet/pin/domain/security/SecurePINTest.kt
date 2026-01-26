package com.mangala.wallet.pin.domain.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SecurePINTest {

    @Test
    fun testSecurePIN_validPIN() {
        val pin = "123456".toCharArray()
        SecurePIN(pin).use { securePIN ->
            assertTrue(securePIN.matches("123456".toCharArray()))
        }
    }

    @Test
    fun testSecurePIN_invalidPIN() {
        val pin = "123456".toCharArray()
        SecurePIN(pin).use { securePIN ->
            assertFalse(securePIN.matches("654321".toCharArray()))
        }
    }

    @Test
    fun testSecurePIN_constantTimeComparison() {
        val pin1 = "123456".toCharArray()
        val pin2 = "123456".toCharArray()

        SecurePIN(pin1).use { securePIN ->
            assertTrue(securePIN.matches(pin2))
        }
    }

    @Test
    fun testSecurePIN_memoryClearing() {
        val pin = "123456".toCharArray()

        SecurePIN(pin).use { securePIN ->
            // PIN is valid during use
            assertTrue(securePIN.matches("123456".toCharArray()))
        }

        // After close, original array should be cleared
        assertEquals('0', pin[0])
        assertEquals('0', pin[5])
    }

    @Test
    fun testSecurePIN_hashGeneration() {
        val pin = "123456".toCharArray()
        val salt = ByteArray(32) { 1 }

        SecurePIN(pin).use { securePIN ->
            val hash1 = securePIN.hash(salt)
            val hash2 = securePIN.hash(salt)

            // Same salt should produce same hash
            assertTrue(hash1.contentEquals(hash2))
        }
    }

    @Test
    fun testConstantTimeEquals_sameArray() {
        val array1 = byteArrayOf(1, 2, 3, 4, 5)
        val array2 = byteArrayOf(1, 2, 3, 4, 5)

        assertTrue(SecurePIN.constantTimeEquals(array1, array2))
    }

    @Test
    fun testConstantTimeEquals_differentArray() {
        val array1 = byteArrayOf(1, 2, 3, 4, 5)
        val array2 = byteArrayOf(1, 2, 3, 4, 6)

        assertFalse(SecurePIN.constantTimeEquals(array1, array2))
    }

    @Test
    fun testConstantTimeEquals_differentLength() {
        val array1 = byteArrayOf(1, 2, 3)
        val array2 = byteArrayOf(1, 2, 3, 4)

        assertFalse(SecurePIN.constantTimeEquals(array1, array2))
    }
}
