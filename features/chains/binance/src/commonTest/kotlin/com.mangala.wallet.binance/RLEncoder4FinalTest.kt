package com.mangala.wallet.binance

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.wallet.binance.data.domain.RLEncoder4Final
import com.mangala.wallet.binance.data.domain.RLEncoder4Final.hexStringToByteArray
import com.mangala.wallet.binance.data.model.RawTransaction
import kotlin.test.Test
import kotlin.test.assertEquals

class RLEncoder4FinalTest {

    @Test
    fun testEncodeTransactionWithMinimumValues() {
        val rawTransaction = RawTransaction(
            chainId = 0,
            sender = "0x70b3F49eA1f1F68b85D11899882DDa418e641f73",
            recipient = "0x80c016FF3E261c0649e4b24b6f8fE4a53822d8bb",
            nonce = 0.toBigInteger(),
            gasPrice = 0.toBigInteger(),
            gasLimit = 0.toBigInteger(),
            value = "0".toBigDecimal(),
            data = byteArrayOf()
        )

        val expectedEncoding = byteArrayOf(
            0xc8.toByte(),
            0x80.toByte(),
            0x80.toByte(),
            0x80.toByte(),
            0x80.toByte(),
            0x80.toByte(),
            0x80.toByte()
        )

        val actualEncoding = RLEncoder4Final.encodeTransaction(rawTransaction)
        assertEquals(expectedEncoding, actualEncoding)
    }

//    @Test
//    fun testEncodeTransactionWithMaximumValues() {
//        val rawTransaction = RawTransaction(
//            chainId = Int.MAX_VALUE,
//            sender = "0xffffffffffffffffffffffffffffffffffffffff",
//            recipient = "0xffffffffffffffffffffffffffffffffffffffff",
//            nonce = Int.MAX_VALUE.toBigInteger(),
//            gasPrice = Long.MAX_VALUE.toBigInteger(),
//            gasLimit = Long.MAX_VALUE.toBigInteger(),
//            value = "115792089237316195423570985008687907853269984665640564039457584007913129639935".toBigDecimal(),
//            data = ByteArray(100) { 0xff.toByte() }
//        )
//
//        // Prepare expectedEncoding byte array for this test case here
//
//        val actualEncoding = RLEncoder4Final.encodeTransaction(rawTransaction)
//        assertEquals(expectedEncoding, actualEncoding)
//    }
//
//    @Test
//    fun testEncodeTransactionWithData() {
//        val rawTransaction = RawTransaction(
//            chainId = 56,
//            sender = "0x0",
//            recipient = "0x1234567890123456789012345678901234567890",
//            nonce = 0.toBigInteger(),
//            gasPrice = 1000000000.toBigInteger(),
//            gasLimit = 200000.toBigInteger(),
//            value = "1000000000000000000".toBigDecimal(),
//            data = "Hello, world!".toByteArray()
//        )
//
//        // Prepare expectedEncoding byte array for this test case here
//
//        val actualEncoding = RLEncoder4Final.encodeTransaction(rawTransaction)
//        assertEquals(expectedEncoding, actualEncoding)
//    }
//
//    @Test
//    fun testEncodeTransactionWithoutData() {
//        val rawTransaction = RawTransaction(
//            chainId = 56,
//            sender = "0x0",
//            recipient = "0x1234567890123456789012345678901234567890",
//            nonce = 0.toBigInteger(),
//            gasPrice = 1000000000.toBigInteger(),
//            gasLimit = 200000.toBigInteger(),
//            value = "1000000000000000000".toBigDecimal(),
//            data = byteArrayOf()
//        )
//
//        // Prepare expectedEncoding byte array for this test case here
//
//        val actualEncoding = RLEncoder4Final.encodeTransaction(rawTransaction)
//        assertEquals(expectedEncoding, actualEncoding)
//    }



}