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
package com.mangala.wallet.antelope_key_manager

import com.memtrip.eos.core.crypto.EosPrivateKey

interface EosKeyManager {
    suspend fun verifyDeviceSupportsRsaEncryption(): Boolean
    fun importPrivateKey(eosPrivateKey: EosPrivateKey): String
    fun getPrivateKey(eosPublicKey: String): EosPrivateKey
    suspend fun publicKeyExists(eosPublicKey: String): Boolean
//    suspend fun getAllPublicKeys(): List<String>
//    suspend fun getPrivateKeys(): List<EosPrivateKey>
    suspend fun createEosPrivateKey(value: String): EosPrivateKey
    suspend fun createEosPrivateKey(): EosPrivateKey
    suspend fun removeKeystoreEntries(): Unit
    suspend fun removePrivateKey(publicKey: String)

    class NotFoundException : RuntimeException()
}