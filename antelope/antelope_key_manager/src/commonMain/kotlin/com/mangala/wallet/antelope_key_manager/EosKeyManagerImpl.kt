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

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.memtrip.eos.core.crypto.EosPrivateKey
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EosKeyManagerImpl: EosKeyManager, KoinComponent {

    private val secureStorageWrapper: SecureStorageWrapper by inject()
    override suspend fun verifyDeviceSupportsRsaEncryption(): Boolean {
        return true
    }

    override fun importPrivateKey(eosPrivateKey: EosPrivateKey): String {
        val keyAlias = eosPrivateKey.publicKey.toString()
        secureStorageWrapper.saveValue("$EOS_TAG$keyAlias", eosPrivateKey.toString())
        return keyAlias
    }

    override fun getPrivateKey(eosPublicKey: String): EosPrivateKey {
        return EosPrivateKey(getPrivateKeyString(eosPublicKey))
    }

    private fun getPrivateKeyString(keyAlias: String): String {
        return secureStorageWrapper.getValue("$EOS_TAG$keyAlias") ?: throw EosKeyManager.NotFoundException()
    }

    private fun getPrivateKeyBytes(keyAlias: String): ByteArray {
        val encodedEncryptedPrivateKey = secureStorageWrapper.getValue("$EOS_TAG$keyAlias") ?: ""
        return encodedEncryptedPrivateKey.encodeToByteArray()
    }

    override suspend fun publicKeyExists(eosPublicKey: String): Boolean {
        return secureStorageWrapper.getValue(EOS_TAG + eosPublicKey) != null
    }

//    override suspend fun getAllPublicKeys(): List<String> {
//        secureStorageWrapper.getValue(EOS_TAG)?.let {
//            return it.split(",").map { it.removePrefix(EOS_TAG) }
//        }
//        return emptyList()
//    }
//
//    override suspend fun getPrivateKeys(): List<EosPrivateKey> {
//        TODO("Not yet implemented")
//    }

    override suspend fun createEosPrivateKey(value: String): EosPrivateKey {
        return EosPrivateKey(value)
    }

    override suspend fun createEosPrivateKey(): EosPrivateKey {
        val privateKey = EosPrivateKey()
        return privateKey
    }

    override suspend fun removeKeystoreEntries() {
        TODO("Not yet implemented")
    }

    override suspend fun removePrivateKey(publicKey: String) {
        secureStorageWrapper.remove("$EOS_TAG$publicKey")
    }

    companion object{
        private const val EOS_TAG = "EOS_"
    }
}