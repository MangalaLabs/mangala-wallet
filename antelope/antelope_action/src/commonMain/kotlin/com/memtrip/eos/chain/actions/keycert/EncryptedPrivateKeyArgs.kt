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
package com.memtrip.eos.chain.actions.keycert

import com.memtrip.eos.core.crypto.KeyType

// https://github.com/greymass/eosio-key-encryption/blob/ac4eec83356d7af1609ef443c0d2c499ae51647c/src/encrypted-private-key.ts#L26
data class EncryptedPrivateKeyArgs(
    val type: KeyType,
    val level: Int,
    val checksum: ByteArray,
    val cipherText: ByteArray
)