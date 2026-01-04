package com.mangala.wallet.binance

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.binance.data.domain.RLPEncoder
import com.mangala.wallet.binance.data.domain.RLPEncoder.hashEncodedTransaction
import com.mangala.wallet.binance.data.model.RawTransaction.Companion.createRawTransaction
import com.soywiz.krypto.encoding.hex
import kotlin.test.Test
import kotlin.test.assertEquals

class RlpEncodingTest {

    @Test
    fun testEncodeByteArray() {
        val input = byteArrayOf(0x01, 0x02, 0x03)
        val encoded = RLPEncoder.encode(input)
        val expected = byteArrayOf(0x83.toByte(), 0x01, 0x02, 0x03)
        assertEquals(expected.contentToString(), encoded.contentToString())
    }

    @Test
    fun testEncodeString() {
        val input = "hello"
        val encoded = RLPEncoder.encode(input)
        val expected = byteArrayOf(0x85.toByte(), 0x68, 0x65, 0x6c, 0x6c, 0x6f)
        assertEquals(expected.contentToString(), encoded.contentToString())
    }

    @Test
    fun testEncodeInteger() {
        val input = 12345
        val encoded = RLPEncoder.encode(input)
        val expected = byteArrayOf(0x82.toByte(), 0x30, 0x39)
        assertEquals(expected.contentToString(), encoded.contentToString())
    }

    @Test
    fun testEncodeList() {
        val input = listOf("hello", byteArrayOf(0x01, 0x02, 0x03), 12345)
        val encoded = RLPEncoder.encode(input)
        val expected = byteArrayOf(0xc9.toByte(), 0x85.toByte(), 0x68, 0x65, 0x6c, 0x6c, 0x6f, 0x83.toByte(), 0x01, 0x02, 0x03, 0x82.toByte(), 0x30, 0x39)
        assertEquals(expected.contentToString(), encoded.contentToString())
    }

//    @Test
//    fun testHashEncodedTransaction(){
//        val sender = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e"
//        val recipient = "0x5aeda56215b167893e80b4fe645ba6d5bab767de"
//        val value = BigInteger.parseString("1000000000000000000") // 1 BNB
//        val gasPrice = BigInteger.parseString("20000000000") // 20 Gwei
//        val gasLimit = BigInteger.parseString("21000")
//        val nonce = BigInteger.parseString("10")
//
//        val rawTransaction = createRawTransaction(
//            sender = sender,
//            recipient = recipient,
//            value = value,
//            gasPrice = gasPrice,
//            gasLimit = gasLimit,
//            nonce = nonce
//        )
//        val rlpEncodedTransaction = RLPEncoder.encode(rawTransaction)
//        val hashedTransaction = hashEncodedTransaction(rlpEncodedTransaction)
//
//        // You can replace the expectedHash value with the expected hash value for the given input.
//        val expectedHash = "YOUR_EXPECTED_HASH_VALUE_IN_HEX"
//        assertEquals(expectedHash, hashedTransaction.hex)
//    }

}
