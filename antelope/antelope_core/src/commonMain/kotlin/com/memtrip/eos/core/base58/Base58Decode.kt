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
package com.memtrip.eos.core.base58

import com.memtrip.eos.core.hash.RIPEMD160Digest
import com.memtrip.eos.core.utils.BytesWithChecksum
import com.memtrip.eos.core.utils.Utils
//import fr.acinq.bitcoin.Base58

import org.bitcoinj.core.Base58
//import org.bitcoinj.core.Utils

class Base58Decode {

    // Decodes base58 with RIPEMD160 checksum check
    fun decode(base58Data: String): BytesWithChecksum {
        val data = Base58.decode(base58Data)
        val checksum = decodeChecksum(data)
        return BytesWithChecksum(data.copyOfRange(0, data.size - 4), checksum)
    }

    fun decode(base58Data: String, suffix: String): BytesWithChecksum {
        val data = Base58.decode(base58Data)
        val checksum = decodeChecksum(data, suffix)
        return BytesWithChecksum(data.copyOfRange(0, data.size - 4), checksum)
    }

    private fun decodeChecksum(data: ByteArray, suffix: String? = null): Long {
        val hashData = ByteArray(data.size - 4)
        data.copyInto(hashData, startIndex = 0, endIndex = data.size - 4)

        val actualDataToHash = if (suffix == null) {
            hashData
        } else {
            hashData + suffix.encodeToByteArray()
        }

        val hashChecksum = Utils.readUint32(RIPEMD160Digest.hash(actualDataToHash), 0)
        val dataChecksum = Utils.readUint32(data, data.size - 4)

        if (hashChecksum != dataChecksum) {
            throw IllegalArgumentException("Invalid format, checksum mismatch")
        }

        return dataChecksum
    }
}