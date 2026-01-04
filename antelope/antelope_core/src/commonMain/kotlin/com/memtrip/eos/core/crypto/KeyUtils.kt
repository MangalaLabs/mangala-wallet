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
package com.memtrip.eos.core.crypto

import com.appmattus.crypto.Algorithm
import com.soywiz.krypto.sha512
import org.bitcoinj.core.ECKey

object KeyUtils {

    fun validateChecksum(checksumProvided: ByteArray, privateKeyBytes: ByteArray, keyType: KeyType): Boolean {
        val checksumCalculated = calculateChecksumWithSuffix(keyType, privateKeyBytes)

        return checksumProvided.contentEquals(checksumCalculated)
    }

    fun calculateChecksumWithSuffix(
        keyType: KeyType,
        keyBytes: ByteArray
    ): ByteArray {
        val suffix = keyType.checksumSuffix

        val checksumDigestBytes = ByteArray(keyBytes.size + suffix.length)
        for (i in keyBytes.indices) {
            checksumDigestBytes[i] = keyBytes[i]
        }
        for (i in suffix.indices) {
            checksumDigestBytes[keyBytes.size + i] = suffix[i].code.toByte()
        }
        val digest = Algorithm.RipeMD160.createDigest()
        digest.update(checksumDigestBytes)
        val checksumCalculated = digest.digest().copyOfRange(0, CHECKSUM_SIZE)
        return checksumCalculated
    }

    // ECDH
    fun deriveSharedSecret(privateKey: EosPrivateKey, publicKey: EosPublicKey): ByteArray {
        val priv = ECKey.fromPrivate(privateKey.bytes ?: byteArrayOf())
        val pub = ECKey.fromPublicOnly(publicKey.bytes)
        val sharedSecret = pub.pubKeyPoint.multiply(priv.privKey).normalize().affineXCoord?.encoded
            ?: byteArrayOf()
        return sharedSecret.sha512().bytes
    }

    private const val CHECKSUM_SIZE = 4
}