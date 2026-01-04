package com.mangala.wallet.binance.data.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger

data class RawTransaction(
    val chainId: Int,
    val sender: String,
    val recipient: String,
    val nonce: BigInteger,
    val gasPrice: BigInteger,
    val gasLimit: BigInteger,
    val value: BigDecimal,
    val data: ByteArray
) {
    companion object {
        fun createRawTransaction(
            chainId: Int,
            sender: String,
            recipient: String,
            value: BigDecimal,
            gasPrice: BigInteger,
            gasLimit: BigInteger,
            nonce: BigInteger,
            data: ByteArray = byteArrayOf()
        ): RawTransaction {
            val toAddress = if (recipient.startsWith("0x")) recipient else "0x$recipient"
            val valueInWei = value * BigDecimal.TEN.pow(18)
            return RawTransaction(
                chainId = chainId,
                sender = sender,
                recipient = toAddress,
                nonce = nonce,
                gasPrice = gasPrice,
                gasLimit = gasLimit,
                value = valueInWei,
                data = data
            )
        }

    }
}