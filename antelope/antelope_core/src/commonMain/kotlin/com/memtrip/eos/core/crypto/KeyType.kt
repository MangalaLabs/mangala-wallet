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

enum class KeyType(
    val privateKeyPrefix: String,
    val publicKeyPrefix: String,
    val checksumSuffix: String,
    val signaturePrefix: String,
    val index: Int
) {
    K1("PVT_K1_", "PUB_K1_", "K1", "SIG_K1_", 0),
//    R1("R1"),
//    WA,
    LEGACY("", "", "", "", 3);

    companion object {
        fun fromIndex(index: Int): KeyType {
            return when (index) {
                0 -> K1
                1 -> throw UnsupportedOperationException("Keytype R1 not supported")
                2 -> throw UnsupportedOperationException("Keytype WA not supported")
                else -> throw IllegalStateException("Unknown key type $index")
            }
        }

        fun fromString(keyType: String): KeyType {
            return when (keyType) {
                "K1" -> K1
                "R1" -> throw UnsupportedOperationException("Keytype R1 not supported")
                "WA" -> throw UnsupportedOperationException("Keytype WA not supported")
                else -> throw IllegalStateException("Unknown key type $keyType")
            }
        }

        fun toIndex(keyType: KeyType): Int {
            return keyType.index
        }
    }
}