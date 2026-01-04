package com.mangala.wallet.binance

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.binance.data.model.RawTransaction
import com.mangala.wallet.binance.data.model.RawTransaction.Companion.createRawTransaction
import kotlin.test.Test
import kotlin.test.assertEquals

class RawTransactionTest {

    @Test
    fun `createRawTransaction returns correct values`() {
        val chainId = 56
        val sender = "0x70b3F49eA1f1F68b85D11899882DDa418e641f73"
        val recipient = "0x80c016FF3E261c0649e4b24b6f8fE4a53822d8bb"
        val value = BigDecimal.parseString("0.5")
        val gasPrice = BigInteger.parseString("1000000000")
        val gasLimit = BigInteger.parseString("21000")
        val nonce = BigInteger.parseString("123")
        val data = byteArrayOf(1, 2, 3)

        val expectedToAddress = "0x80c016FF3E261c0649e4b24b6f8fE4a53822d8bb"
        val expectedValueInWei = BigDecimal.parseString("500000000000000000")

        val transaction = RawTransaction.createRawTransaction(
            chainId = chainId,
            sender = sender,
            recipient = recipient,
            value = value,
            gasPrice = gasPrice,
            gasLimit = gasLimit,
            nonce = nonce,
            data = data
        )

        assertEquals(chainId, transaction.chainId)
        assertEquals(nonce, transaction.nonce)
        assertEquals(gasPrice, transaction.gasPrice)
        assertEquals(gasLimit, transaction.gasLimit)
        assertEquals(expectedToAddress, transaction.recipient)
        assertEquals(expectedValueInWei, transaction.value)
        assertEquals(data.toList(), transaction.data.toList())
    }
}