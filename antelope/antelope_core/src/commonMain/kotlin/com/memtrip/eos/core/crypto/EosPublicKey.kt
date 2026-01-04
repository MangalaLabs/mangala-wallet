/*
 * Copyright 2013-present memtrip LTD.
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

// ------------------------------------------------------------------
// MODIFICATION NOTICE:
// Modified by Mangala Wallet
// Description: Adapted for Kotlin Multiplatform compatibility.
// ------------------------------------------------------------------
package com.memtrip.eos.core.crypto

import com.memtrip.eos.core.base58.Base58Decode
import com.memtrip.eos.core.base58.Base58Encode
import com.memtrip.eos.core.crypto.KeyUtils.calculateChecksumWithSuffix
import com.memtrip.eos.core.hash.RIPEMD160Digest
import com.memtrip.eos.core.utils.BytesWithChecksum
import com.memtrip.eos.core.utils.Utils
import org.bitcoinj.core.Base58
import org.bitcoinj.core.ECKey
//import org.bitcoinj.core.Utils

class EosPublicKey {

    private val ecKey: ECKey
    private val checkSum: Long
    private val base58: String
    private val keyType: KeyType

    private val base58Encode = Base58Encode()

    val bytes: ByteArray
        get() = ecKey.pubKey

    @Suppress("unused")
    val isCurveParamK1: Boolean
        get() = true

    constructor(bytes: ByteArray, keyType: KeyType = KeyType.LEGACY) {
        this.ecKey = ECKey.fromPublicOnly(bytes.copyOf(33))
        this.checkSum = Utils.readUint32(RIPEMD160Digest.hash(ecKey.pubKey), 0)
        this.base58 = base58Encode.encodeKey(PREFIX, ecKey.pubKey)
        this.keyType = keyType
    }

    constructor(publicKey: String) {
        val (bytesWithChecksum, keyType) = when {
            publicKey.startsWith(KeyType.K1.publicKeyPrefix) -> publicKey.fromK1PublicKeyString() to KeyType.K1
            else -> bytesFromBase58(publicKey) to KeyType.LEGACY
        }
        this.ecKey = ECKey.fromPublicOnly(bytesWithChecksum.bytes)
        this.checkSum = bytesWithChecksum.checkSum
        this.base58 = base58Encode.encodeKey(PREFIX, ecKey.pubKey)
        this.keyType = keyType
    }

    private fun String.fromK1PublicKeyString(): BytesWithChecksum {
        val privateKeyString = removePrefix(KeyType.K1.publicKeyPrefix)
        val decodedKey = Base58.decode(privateKeyString)
        val privateKeyBytes = decodedKey.copyOfRange(0, PUBLIC_KEY_DATA_SIZE)
        val checksumProvided =
            decodedKey.copyOfRange(PUBLIC_KEY_DATA_SIZE, PUBLIC_KEY_DATA_SIZE + CHECKSUM_SIZE)

        if (KeyUtils.validateChecksum(checksumProvided, privateKeyBytes, KeyType.K1).not()) {
            throw IllegalArgumentException("Invalid format, checksum mismatch")
        }

        return BytesWithChecksum(privateKeyBytes, Utils.readUint32(checksumProvided, 0))
    }

    internal constructor(ecKey: ECKey, keyType: KeyType = KeyType.LEGACY) {
        this.ecKey = ecKey
        this.checkSum = Utils.readUint32(RIPEMD160Digest.hash(ecKey.pubKey), 0)
        this.base58 = base58Encode.encodeKey(PREFIX, ecKey.pubKey)
        this.keyType = keyType
    }

    override fun toString(): String {
        return when (keyType) {
            KeyType.K1 -> fromK1KeyToString()
            KeyType.LEGACY -> base58
        }
    }

    fun toLegacyString(): String {
        return base58
    }

    private fun fromK1KeyToString(): String {
        val keyType = KeyType.K1

        val digest = calculateChecksumWithSuffix(keyType, this.bytes)

        val whole = ByteArray(this.bytes.size + CHECKSUM_SIZE)
        this.bytes.copyInto(whole, 0, 0, this.bytes.size)
        digest.copyInto(whole, this.bytes.size, 0, 4)

        return keyType.publicKeyPrefix + Base58.encode(whole)
    }

    override fun hashCode(): Int {
        return (checkSum and 0xFFFFFFFFL).toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EosPublicKey) return false
        return bytes.contentEquals(other.bytes)
    }

    companion object {
        private const val PREFIX = "EOS"
        private const val PUBLIC_KEY_DATA_SIZE = 33
        private const val CHECKSUM_SIZE = 4

        private fun bytesFromBase58(base58: String): BytesWithChecksum {
            val parts = base58.split("_")

            return if (base58.startsWith(PREFIX)) {
                if (parts.size == 1) {
                    Base58Decode().decode(base58.substring(PREFIX.length))
                } else {
                    throw IllegalArgumentException("Unsupported format: $base58")
                }
            } else {
                throw IllegalArgumentException("Unsupported key type.")
            }
        }
    }
}