package com.mangala.wallet.binance.data.domain

import com.appmattus.crypto.Algorithm
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.wallet.binance.data.model.RawTransaction
import fr.acinq.secp256k1.Secp256k1

object RLPEncoder {

    fun encode(input: Any): ByteArray {
        return when (input) {
            is ByteArray -> encodeBytes(input)
            is String -> encodeBytes(input.encodeToByteArray())
            is BigInteger -> encodeBytes(input.toByteArray())
            is Int -> encodeBytes(BigInteger.parseString(input.toString()).toByteArray())
            is Long -> encodeBytes(BigInteger.parseString(input.toString()).toByteArray())
            is RawTransaction -> encodeList(
                listOf(
                    input.nonce,
                    input.gasPrice,
                    input.gasLimit,
//                    input.recipient,
                    input.value,
                    input.data ?: ""
                )
            )
            is List<*> -> encodeList(input)
            else -> throw IllegalArgumentException("Unsupported input type")
        }
    }

    private fun encodeBytes(input: ByteArray): ByteArray {
        return when {
            input.size == 1 && input[0].toInt() and 0xFF < 0x80 -> input
            input.size < 56 -> byteArrayOf((0x80 + input.size).toByte()) + input
            else -> {
                val sizeBytes = BigInteger.parseString(input.size.toString()).toByteArray()
                byteArrayOf((0xB7 + sizeBytes.size).toByte()) + sizeBytes + input
            }
        }
    }

    private fun encodeList(input: List<*>): ByteArray {
        val encodedElements = input.map { element ->
            element?.let { encode(it) } ?: throw IllegalArgumentException("List contains null elements")
        }
        val encodedConcatenatedElements = encodedElements.reduce { acc, bytes -> acc + bytes }
        return when {
            encodedConcatenatedElements.size < 56 -> byteArrayOf((0xC0 + encodedConcatenatedElements.size).toByte()) + encodedConcatenatedElements
            else -> {
                val sizeBytes = encodedConcatenatedElements.size.toBigInteger().toByteArray()
                val prefix = 0xF7 + sizeBytes.size
                byteArrayOf(prefix.toByte()) + sizeBytes + encodedConcatenatedElements
            }
        }
    }

    fun rlpEncodeRawTransaction(rawTransaction: RawTransaction): ByteArray {
        val rlpData = rawTransaction.data?.let { RLPEncoder.encode(it) } ?: byteArrayOf()
        return RLPEncoder.encode(
            listOf(
                RLPEncoder.encode(rawTransaction.nonce),
                RLPEncoder.encode(rawTransaction.gasPrice),
                RLPEncoder.encode(rawTransaction.gasLimit),
//                RLPEncoder.encode(rawTransaction.recipient),
                RLPEncoder.encode(rawTransaction.value),
                rlpData
            )
        )
    }

    fun hashEncodedTransaction(encodedTransaction: ByteArray): ByteArray {
        return Algorithm.Keccak256.createDigest().digest(encodedTransaction)
    }

    fun signTransaction(privateKey: BigInteger, encodedTransaction: ByteArray): Pair<ByteArray, ByteArray> {
        val hashedTransaction = hashEncodedTransaction(encodedTransaction)
        Secp256k1.sign(hashedTransaction, privateKey.toByteArray()).let { signature ->
            val r = signature.copyOfRange(0, 32)
            val s = signature.copyOfRange(32, 64)
            return Pair(r, s)
        }
    }

    fun calculateRecoveryId(signature: Pair<ByteArray, ByteArray>, senderPublicKey: ByteArray): Int {
        for (i in 0..1) {
            val recoveryId = i + 27
            Secp256k1.ecdsaRecover(signature.first, signature.second, recoveryId).let { recoveredPublicKey ->
                if (recoveredPublicKey.contentEquals(senderPublicKey)) {
                    return recoveryId
                }
            }
        }
        throw IllegalArgumentException("Unable to calculate recovery id")
    }

//    fun createSignedTransaction(
//        privateKey: BigInteger,
//        rawTransaction: RawTransaction,
//        chainId: BigInteger
//    ): ByteArray {
//        val encodedTransaction = encode(rawTransaction)
//        val hashedTransaction = hashEncodedTransaction(encodedTransaction)
//        val signature = signTransaction(privateKey, hashedTransaction)
//        val recoveryId = calculateRecoveryId(signature, hashedTransaction)
//        val updatedRawTransaction = RawTransaction(
//            nonce = rawTransaction.nonce,
//            gasPrice = rawTransaction.gasPrice,
//            gasLimit = rawTransaction.gasLimit,
//            to = rawTransaction.to,
//            value = rawTransaction.value,
//            data = rawTransaction.data,
//            v = (chainId + 35 + recoveryId.toBigInteger()).toByteArray(),
//            r = signature.first,
//            s = signature.second
//        )
//        return encode(updatedRawTransaction)
//    }

}
