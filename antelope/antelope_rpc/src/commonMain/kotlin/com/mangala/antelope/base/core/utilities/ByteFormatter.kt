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

package com.mangala.antelope.base.core.utilities

import com.mangala.wallet.utils.ext.toHexString
import com.soywiz.krypto.encoding.Base64
import com.soywiz.krypto.encoding.Hex
import com.soywiz.krypto.sha256


class ByteFormatter(private val context: ByteArray) {

    companion object {
        private const val BASE64_PADDING = 4
        private const val BASE64_PADDING_CHAR = '='

        fun createFromBase64(base64String: String): ByteFormatter {
            // In Kotlin, use the built-in functions for string manipulation
            val trimmed = base64String.filterNot { it == BASE64_PADDING_CHAR }
            val padded = trimmed.padEnd(
                (trimmed.length + BASE64_PADDING - 1) / BASE64_PADDING * BASE64_PADDING,
                BASE64_PADDING_CHAR
            )
            return ByteFormatter(Base64.decode(padded)) // Adjusted for Kotlin's Base64 API
        }

        fun createFromHex(hexString: String): ByteFormatter {
            // Replace Hex.decode() with Kotlin equivalent; assuming it's a utility method you have
            val data = Hex.decode(hexString)
            return ByteFormatter(data)
        }
    }

    fun toHex(): String {
        // Replace Hex.toHexString() with Kotlin equivalent; assuming it's a utility method you have
        return this.context.toHexString()
    }

    fun sha256(): ByteFormatter {
        // Replace Sha256Hash.hash() with Kotlin equivalent; assuming it's a utility method you have
//        return ByteFormatter(Sha256Hash.hash(context))
        return ByteFormatter(this.context.sha256().bytes)
    }
}