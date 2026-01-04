/**
Copyright (c) 2021 FFF00 Agents AB & Greymass Inc. All Rights Reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistribution of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistribution in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.

YOU ACKNOWLEDGE THAT THIS SOFTWARE IS NOT DESIGNED, LICENSED OR INTENDED FOR USE
IN THE DESIGN, CONSTRUCTION, OPERATION OR MAINTENANCE OF ANY MILITARY FACILITY.
 */

package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.keycert

import com.mangala.wallet.features.chains.antelope_base.domain.utils.base64uToByteArray
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.features.chains.antelope_base.domain.utils.Base2048
import com.mangala.wallet.features.chains.antelope_base.domain.utils.Base2048.base2048ToWordList
import com.mangala.wallet.features.chains.antelope_base.domain.utils.getChecksum
import com.mangala.wallet.utils.ext.toRawHexString
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.keycert.KeyCertArgs
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.chain.actions.transaction.decoder.AbiBinaryTransactionReader
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.soywiz.krypto.AES
import com.soywiz.krypto.Padding

class DecryptKeyCertUseCase {

    // Create a new KeyCert instance from a anchorcert: string.
    operator fun invoke(certString: String): KeyCertArgs {
        if (!certString.startsWith("anchorcert:", ignoreCase = true)) {
            throw IllegalArgumentException("Not an anchor key certificate string")
        }

        var payload = certString.substringAfter("anchorcert:")
        while (payload.startsWith("/", ignoreCase = false)) {
            payload = payload.substring(1)
        }

        val data = payload.base64uToByteArray()

        val abiReader = AbiBinaryTransactionReader(data.toRawHexString())
        val keyCert = abiReader.readKeyCertArgs()

        return keyCert
    }

    operator fun invoke(
        blockchainType: BlockchainType,
        permission: TransactionAuthorizationAbi,
        mnemonic: List<String>
    ): KeyCertArgs {
        val decodedMnemonic = Base2048.decode(mnemonic)

        val abiReader = AbiBinaryTransactionReader(decodedMnemonic.toRawHexString())
        val encryptedPrivateKey = abiReader.readEncryptedPrivateKey()

        return KeyCertArgs(blockchainType.chainId, permission, encryptedPrivateKey)
    }

    fun getEncryptedPrivateKeyMnemonic(keyCert: KeyCertArgs): List<String> {
        val bytes = AbiBinaryGenTransactionWriter(CompressionType.NONE).apply {
            squishEncryptedPrivateKeyArgsSquishable(keyCert.encryptedPrivateKey)
        }.toBytes()

        return Base2048.encode(bytes).base2048ToWordList()
    }

    fun decryptKeyCert(keyCert: KeyCertArgs, encryptionKeyWords: List<String>): EosPrivateKey {
        if (encryptionKeyWords.size != 6) {
            throw IllegalArgumentException("Excpected 6 encryption words, got ${encryptionKeyWords.size}")
        }
        val password = Base2048.decode(encryptionKeyWords.map { it.trim().lowercase() })
        val params = getParamsBasedOnLevel(keyCert.encryptedPrivateKey.level)

        val (key, iv) = getKeyCertKeyAndIv(
            password = password,
            checksum = keyCert.encryptedPrivateKey.checksum,
            N = params.first,
            r = params.second,
            p = params.third
        )

        val privateKeyBytes =
            AES.decryptAesCbc(keyCert.encryptedPrivateKey.cipherText, key, iv, Padding.NoPadding)
        val privateKey = EosPrivateKey(privateKeyBytes, keyType = keyCert.encryptedPrivateKey.type)
        val decryptedPrivateKeyChecksum = privateKey.getChecksum()

        if (!decryptedPrivateKeyChecksum.contentEquals(keyCert.encryptedPrivateKey.checksum)) {
            throw IllegalArgumentException("Checksum mismatch")
        }

        return privateKey
    }

    // returns N, r, p
    private fun getParamsBasedOnLevel(level: Int): Triple<Int, Int, Int> {
        val nExp = ((level and 0b1110_0000) shr 5) + 14 // First 3 bits is N starting at 14
        val rExp = ((level and 0b0001_1100) shr 2) + 3 // Next 3 bits is r starting at 3
        val pExp = level and 0b0000_0011 // Last two bits is p

        // Raise to power of 2
        val N = 1 shl nExp
        val r = 1 shl rExp
        val p = 1 shl pExp

        return Triple(N, r, p)
    }
}