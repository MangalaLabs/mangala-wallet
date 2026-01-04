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
package com.memtrip.eos.chain.actions.transaction.gen

import com.memtrip.eos.abi.writer.ByteWriter
import com.memtrip.eos.abi.writer.Squishable
import com.memtrip.eos.chain.actions.keycert.EncryptedPrivateKeyArgs
import com.memtrip.eos.core.crypto.KeyType

class EncryptedPrivateKeyArgsSquishable internal constructor(private val abiBinaryGen: AbiBinaryGenTransactionWriter) :
    Squishable<EncryptedPrivateKeyArgs> {
    override fun squish(encryptedPrivateKey: EncryptedPrivateKeyArgs, writer: ByteWriter) {
        writer.putByte(KeyType.toIndex(encryptedPrivateKey.type).toByte())
        writer.putBytes(byteArrayOf(encryptedPrivateKey.level.toByte()) + encryptedPrivateKey.checksum + encryptedPrivateKey.cipherText)
    }
}