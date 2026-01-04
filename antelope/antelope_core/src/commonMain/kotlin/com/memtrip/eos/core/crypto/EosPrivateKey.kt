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

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.memtrip.eos.core.crypto.KeyUtils.calculateChecksumWithSuffix
import com.memtrip.eos.core.crypto.KeyUtils.validateChecksum
import com.memtrip.eos.core.crypto.signature.SecP256K1KeyCurve
import org.bitcoinj.core.Base58
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Sha256Hash

class EosPrivateKey internal constructor(
    private val key: ECKey,
    private val base58: String = base58Encode(key),
    val keyType: KeyType = KeyType.LEGACY
) {

    val publicKey: EosPublicKey = EosPublicKey(key.pubKey, keyType)
    val keyCurve: SecP256K1KeyCurve = SecP256K1KeyCurve()

    val bytes: ByteArray?
        get() = key.privKeyBytes

    val bigInteger: BigInteger
        get() = key.privKey

    constructor() : this(ECKey())

    @Deprecated("Please use EosPrivateKey.fromString instead as it supports other formats")
    constructor(base58: String) : this(getBase58Bytes(base58))

    constructor(bytes: ByteArray, keyType: KeyType = KeyType.LEGACY) : this(ECKey.fromPrivate(bytes), keyType = keyType)

    override fun toString(): String {
        return when (keyType) {
            KeyType.K1 -> fromK1KeyToString()
            KeyType.LEGACY -> base58
        }
    }

    fun toString(keyType: KeyType): String {
        return when(keyType) {
            KeyType.K1 -> fromK1KeyToString()
            KeyType.LEGACY -> base58
        }
    }

    fun toLegacyString(): String {
        return base58
    }

    fun fromK1KeyToString(): String {
        val keyType = KeyType.K1

        val digest = calculateChecksumWithSuffix(keyType, this.bytes!!)

        val whole = ByteArray(this.bytes!!.size + CHECKSUM_SIZE)
        this.bytes!!.copyInto(whole, 0, 0, this.bytes!!.size)
        digest.copyInto(whole, this.bytes!!.size, 0, 4)

        return keyType.privateKeyPrefix + Base58.encode(whole)
    }

    fun derive(publicKey: EosPublicKey) {
        val pub = ECKey.fromPublicOnly(publicKey.bytes)

//        pub.pubKeyPoint.multiply()
    }

    companion object {
        fun fromString(privateKey: String): EosPrivateKey {
            return when {
                privateKey.startsWith(KeyType.K1.privateKeyPrefix) -> privateKey.fromK1PrivateKeyString()
                else -> EosPrivateKey(getBase58Bytes(privateKey), KeyType.LEGACY)
            }
        }

        private fun getBase58Bytes(base58: String): ByteArray {
            if (base58.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size == 1) {
                val data = Base58.decode(base58)

                val checkOne = Sha256Hash.hash(data, 0, data.size - 4)
                val checkTwo = Sha256Hash.hash(checkOne)

                if (equalsFromOffset(checkTwo, data, data.size - 4) || equalsFromOffset(checkOne, data, data.size - 4)) {

                    val keyBytes = data.copyOfRange(1, data.size - 4)

                    if (keyBytes.size < 5) {
                        throw IllegalArgumentException("Invalid private key length.")
                    }
                    return keyBytes
                }

                throw IllegalArgumentException("Invalid format, checksum mismatch")
            } else {
                throw IllegalArgumentException("Invalid private format, expecting a prefix")
            }
        }

        private fun base58Encode(key: ECKey): String {
            val privateKeyBytes = key.privKeyBytes
            val resultWIFBytes = ByteArray(1 + 32 + 4)
            resultWIFBytes[0] = 0x80.toByte()
            privateKeyBytes?.copyInto(
                destination = resultWIFBytes,
                destinationOffset = 1,
                startIndex = if ((privateKeyBytes.size > 32)) 1 else 0,
                endIndex = if ((privateKeyBytes.size > 32)) 33 else 32
            )
            val hash = Sha256Hash.hashTwice(resultWIFBytes, 0, 33)
            hash.copyInto(
                destination = resultWIFBytes,
                destinationOffset = 33,
                startIndex = 0,
                endIndex = 4
            )
            return Base58.encode(resultWIFBytes)
        }

        private fun equalsFromOffset(mHashBytes: ByteArray, toCompareData: ByteArray?, offsetInCompareData: Int): Boolean {
            if (toCompareData == null ||
                offsetInCompareData < 0 ||
                mHashBytes.size <= 4 ||
                toCompareData.size <= offsetInCompareData) {

                return false
            }

            for (i in 0..3) {
                if (mHashBytes[i] != toCompareData[offsetInCompareData + i]) {
                    return false
                }
            }

            return true
        }

        private fun String.fromK1PrivateKeyString(): EosPrivateKey {
            val privateKeyString = removePrefix(KeyType.K1.privateKeyPrefix)
            val decodedKey = Base58.decode(privateKeyString)
            val privateKeyBytes = decodedKey.copyOfRange(0, PRIVATE_KEY_DATA_SIZE)
            val checksumProvided =
                decodedKey.copyOfRange(PRIVATE_KEY_DATA_SIZE, PRIVATE_KEY_DATA_SIZE + CHECKSUM_SIZE)

            validateChecksum(checksumProvided, privateKeyBytes, KeyType.K1)

            return EosPrivateKey(privateKeyBytes, KeyType.K1)
        }

        fun String.toEosPrivateKeyOrNull(): EosPrivateKey? {
            return try {
                fromString(this)
            } catch (e: Exception) {
                return null
            }
        }

        private const val PRIVATE_KEY_DATA_SIZE = 32
        private const val CHECKSUM_SIZE = 4
    }
}