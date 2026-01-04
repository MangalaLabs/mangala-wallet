package com.mangala.wallet.features.chains.evmcompatible.crypto

import com.appmattus.crypto.Algorithm
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.wallet.features.chains.evmcompatible.model.Signature
import com.mangala.wallet.features.chains.evmcompatible.utils.Arrays
import fr.acinq.secp256k1.Secp256k1
import fr.acinq.secp256k1.Secp256k1Exception

object CryptoUtils {

    fun ellipticSign(messageToSign: ByteArray, privateKey: ByteArray): ByteArray {
        val signature = Secp256k1.sign(messageToSign, privateKey)
        val pubkey = Secp256k1.pubkeyCreate(privateKey)
        val recoveryId = calculateRecoveryId(signature, messageToSign, pubkey)
        val v = recoveryId.toByte()
        return signature + v
    }

    private fun calculateRecoveryId(signature: ByteArray, message: ByteArray, senderPublicKey: ByteArray): Int {
        var recoveryId = -1
        for (i in 0..3) {
            Secp256k1.ecdsaRecover(signature, message, i).let { recoveredPublicKey ->
                if (recoveredPublicKey.contentEquals(senderPublicKey)) {
                    recoveryId = i
                    return recoveryId
                }
            }
        }
        return -1
    }

    fun sha3(data: ByteArray): ByteArray {
        return Algorithm.Keccak256.createDigest().digest(data)
    }
}