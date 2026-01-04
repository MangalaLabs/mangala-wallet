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
//import fr.acinq.bitcoin.Base58
import org.bitcoinj.core.Base58

class Base58Encode {

    fun encodeSignature(prefix: String, data: ByteArray): String {
        return encodeWithChecksum(prefix, "K1", data)
    }

    fun encodeKey(prefix: String, data: ByteArray): String {
        return encodeWithChecksum(prefix, "", data)
    }

    private fun encodeWithChecksum(prefix: String, signaturePrefix: String, data: ByteArray): String {

        val dataWithChecksum = ByteArray(data.size + 4)

        data.copyInto(dataWithChecksum, startIndex = 0, endIndex = data.size)
        encodeChecksum(data, signaturePrefix).copyInto(dataWithChecksum, destinationOffset = data.size, endIndex = 4)

        return if (signaturePrefix.isEmpty()) {
            prefix + Base58.encode(dataWithChecksum)
        } else {
            prefix + "_" + signaturePrefix + "_" + Base58.encode(dataWithChecksum)
        }
    }

    private fun encodeChecksum(data: ByteArray, vararg extras: String): ByteArray {

        val toHashData = ByteArray(data.size + extras.sumBy { it.length })

        data.copyInto(toHashData, startIndex = 0, endIndex = data.size)

        extras.filter { extra ->
            extra.isNotEmpty()
        }.forEach { extra ->
            extra.encodeToByteArray().copyInto(toHashData, destinationOffset = data.size, startIndex = 0, endIndex = extra.length)
        }

        return RIPEMD160Digest.hash(toHashData)
    }
}