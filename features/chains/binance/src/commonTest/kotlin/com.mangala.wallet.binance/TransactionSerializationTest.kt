package com.mangala.wallet.binance

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.binance.data.model.Transaction
import com.mangala.wallet.binance.data.model.hexStringToByteArray
import com.mangala.wallet.binance.data.model.toSerializedByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionSerializationTest {

    @Test
    fun `test serialization with minimum values`() {
        val transaction = Transaction(
            nonce = BigInteger.ZERO,
            gasPrice = BigInteger.ZERO,
            gasLimit = BigInteger.ZERO,
            recipient = "0x0000000000000000000000000000000000000000",
            value = BigInteger.ZERO,
            data = ByteArray(0)
        )

        val serialized = transaction.toSerializedByteArray()

        val expected = "0x8080809400000000000000000000000000000000000000000000000000000000000000808080".hexStringToByteArray()
        assertTrue { serialized.contentEquals(expected) }
    }

    @Test
    fun testSerializationWithMinimumValues() {
        val transaction = Transaction(
            nonce = BigInteger.ZERO,
            gasPrice = BigInteger.ZERO,
            gasLimit = BigInteger.ZERO,
            recipient = "0x0000000000000000000000000000000000000000",
            value = BigInteger.ZERO,
            data = byteArrayOf()
        )
        val serializedTransaction = transaction.toSerializedByteArray()
        val expectedSerialization = "0x808080808080".hexStringToByteArray()
        assertEquals(true, expectedSerialization.contentEquals(serializedTransaction))
    }


    @Test
    fun `test serialization with maximum values`() {
        val maxBigInteger = BigInteger.parseString("0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16)

        val transaction = Transaction(
            nonce = maxBigInteger,
            gasPrice = maxBigInteger,
            gasLimit = maxBigInteger,
            recipient = "0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
            value = maxBigInteger,
            data = ByteArray(32) { 0xFF.toByte() }
        )

        val serialized = transaction.toSerializedByteArray()

        val expected = "0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF94FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF".hexStringToByteArray()
        assertTrue { serialized.contentEquals(expected) }
    }

    @Test
    fun `test serialization with sample transaction`() {
        val transaction = Transaction(
            nonce = BigInteger.parseString("2", 10),
            gasPrice = BigInteger.parseString("20000000000", 10),
            gasLimit = BigInteger.parseString("21000", 10),
            recipient = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e",
            value = BigInteger.parseString("1000000000000000000", 10),
            data = ByteArray(0)
        )

        val serialized = transaction.toSerializedByteArray()

        val expected = "0x0284ee6b280082520894742d35cc6634c0532925a3b844bc454e4438f44e880de0b6b3a7640000808080".hexStringToByteArray()
        assertTrue { serialized.contentEquals(expected) }
    }

    @Test
    fun `test serialization with data`() {
        val transaction = Transaction(
            nonce = BigInteger.parseString("1", 10),
            gasPrice = BigInteger.parseString("1000000000", 10),
            gasLimit = BigInteger.parseString("250000", 10),
            recipient = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e",
            value = BigInteger.parseString("500000000000000000", 10),
            data = "0x606060".hexStringToByteArray()
        )

        val serialized = transaction.toSerializedByteArray()

        val expected =
            "0x01843b9aca0083c35094742d35cc6634c0532925a3b844bc454e4438f44e8706f05b59d3b2000306060".hexStringToByteArray()
        assertTrue { serialized.contentEquals(expected) }
    }
}