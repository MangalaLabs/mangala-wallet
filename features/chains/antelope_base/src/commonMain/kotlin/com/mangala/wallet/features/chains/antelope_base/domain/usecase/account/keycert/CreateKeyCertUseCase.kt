package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.keycert

import com.mangala.wallet.cryptography.generateSecureRandomBytes
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.keycert.SecurityLevel
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.features.chains.antelope_base.domain.utils.Base2048
import com.mangala.wallet.features.chains.antelope_base.domain.utils.encodeBase64u
import com.mangala.wallet.features.chains.antelope_base.domain.utils.getChecksum
import com.mangala.wallet.utils.bip39.BIP39_WORDLIST_ENGLISH
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.keycert.EncryptedPrivateKeyArgs
import com.memtrip.eos.chain.actions.keycert.KeyCertArgs
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.KeyType
import com.soywiz.krypto.AES
import com.soywiz.krypto.Padding
import kotlin.math.abs

class CreateKeyCertUseCase {
    operator fun invoke(
        privateKey: EosPrivateKey,
        blockchainType: BlockchainType,
        accountName: String,
        permissionName: String,
        encryptionWords: List<String> = generateEncryptionWords()
    ): KeyCertArgs {
        if (encryptionWords.size != 6) {
            throw IllegalArgumentException("Expected 6 encryption words, got ${encryptionWords.size}")
        }
        val password = Base2048.decode(encryptionWords)
        val encryptedPrivateKey = encryptPrivateKey(privateKey, password, SecurityLevel.DEFAULT)

        return KeyCertArgs(
            chainId = blockchainType.chainId,
            permissionLevel = TransactionAuthorizationAbi(
                actor = accountName,
                permission = permissionName
            ),
            encryptedPrivateKey = encryptedPrivateKey,
            encryptionWords = encryptionWords
        )
    }

    fun toAnchorCertString(keyCertArgs: KeyCertArgs): String {
        val serializedKeyCert = AbiBinaryGenTransactionWriter(CompressionType.NONE).apply {
            squishKeyCertArgsSquishable(keyCertArgs)
        }.toBytes()
        return "anchorcert:" + encodeBase64u(serializedKeyCert)
    }

    private fun generateEncryptionWords(): List<String> {
        val rv = mutableListOf<String>()

        while (rv.size < 6) {
            val word = BIP39_WORDLIST_ENGLISH[abs(generateSecureRandomBytes(4).toInt() % 2048)]
            if (rv.contains(word)) {
                continue
            }
            rv.add(word)
        }

        return rv
    }

    private fun ByteArray.toInt(): Int {
        if (this.size != 4) throw IllegalArgumentException("Expected 4 bytes, got ${this.size}")

        val result = (this[0].toInt() and 0xff) or
                ((this[1].toInt() and 0xff) shl 8) or
                ((this[2].toInt() and 0xff) shl 16) or
                ((this[3].toInt() and 0xff) shl 24)

        println("toInt ${this.joinToString()} -> $result")

        return result
    }

    private fun encryptPrivateKey(
        privateKey: EosPrivateKey,
        password: ByteArray,
        securityLevel: SecurityLevel
    ): EncryptedPrivateKeyArgs {
        // Map legacy to K1, because legacy is not supported in KeyCert
        val validatedKey = if (privateKey.keyType == KeyType.LEGACY && privateKey.bytes != null) {
            val k1FormattedKeyString = privateKey.fromK1KeyToString()
            EosPrivateKey.fromString(k1FormattedKeyString)
        } else {
            privateKey
        }
        val serializedKey = validatedKey.bytes
            ?: throw IllegalArgumentException("Invalid private key")
        val (key, iv) = getKeyCertKeyAndIv(password, validatedKey.getChecksum(), securityLevel)
        val cipher = AES.encryptAesCbc(serializedKey, key, iv, Padding.NoPadding)

        return EncryptedPrivateKeyArgs(
            validatedKey.keyType,
            securityLevel.value,
            checksum = validatedKey.getChecksum(),
            cipherText = cipher
        )
    }
}