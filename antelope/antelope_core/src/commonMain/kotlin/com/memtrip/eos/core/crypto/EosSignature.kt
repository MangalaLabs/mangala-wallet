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

class EosSignature(
    val keyType: KeyType,
    val data: ByteArray
) {
    companion object {
        fun fromString(signature: String): EosSignature {
            if (signature.startsWith("SIG_K1_").not()) {
                throw IllegalArgumentException("Invalid signature format")
            }

            val parts = signature.split("_")

            if (parts.size != 3) throw IllegalArgumentException("Invalid signature format")

            val signatureType = parts.getOrNull(1) ?: throw IllegalArgumentException("Invalid signature format")
            val dataString = parts.getOrNull(2) ?: throw IllegalArgumentException("Invalid signature format")
            val data = Base58Decode().decode(dataString, suffix = signatureType).bytes

            return EosSignature(KeyType.fromString(signatureType), data)
        }
    }
}
