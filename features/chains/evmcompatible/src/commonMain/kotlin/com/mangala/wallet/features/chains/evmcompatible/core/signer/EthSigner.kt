package com.mangala.wallet.features.chains.evmcompatible.core.signer

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.crypto.CryptoUtils
import com.mangala.wallet.features.chains.evmcompatible.crypto.EIP712Encoder
import com.mangala.wallet.features.chains.evmcompatible.crypto.TypedData

class EthSigner(
    private val privateKey: ByteArray,
    private val cryptoUtils: CryptoUtils,
    private val eip712Encoder: EIP712Encoder
) {

    fun signByteArray(message: ByteArray): ByteArray {
        val prefix = "\u0019Ethereum Signed Message:\n" + message.size
        val hashedMessage = cryptoUtils.sha3(prefix.encodeToByteArray() + message)
        return sign(hashedMessage)
    }

    fun signByteArrayLegacy(message: ByteArray): ByteArray {
        return sign(message)
    }

    fun signTypedData(rawJsonMessage: String): ByteArray {
        val encodedMessage = eip712Encoder.encodeTypedDataHash(rawJsonMessage)
        return sign(encodedMessage)
    }

    fun parseTypedData(rawJsonMessage: String): TypedData? {
        return eip712Encoder.parseTypedData(rawJsonMessage)
    }

    private fun sign(message: ByteArray): ByteArray = cryptoUtils.ellipticSign(message, privateKey)

}
