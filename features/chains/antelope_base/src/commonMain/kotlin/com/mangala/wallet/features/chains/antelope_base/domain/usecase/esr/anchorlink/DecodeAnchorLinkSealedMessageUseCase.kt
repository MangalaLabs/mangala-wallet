package com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr.anchorlink

import com.mangala.wallet.antelope_key_manager.EosKeyManager
import com.mangala.wallet.features.chains.antelope_base.domain.model.esr.anchorlink.AnchorLinkSession
import com.mangala.wallet.utils.ext.toHexString
import com.mangala.wallet.utils.ext.toRawHexString
import com.memtrip.eos.abi.writer.bytewriter.DefaultByteWriter
import com.memtrip.eos.chain.actions.transaction.decoder.AbiBinaryTransactionReader
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.EosPublicKey
import com.memtrip.eos.core.crypto.KeyUtils
import com.soywiz.krypto.AES
import com.soywiz.krypto.Padding
import com.soywiz.krypto.sha512

class DecodeAnchorLinkSealedMessageUseCase(
    private val eosKeyManager: EosKeyManager
) {

    operator fun invoke(session: AnchorLinkSession, data: ByteArray): String {
        val transactionReader = AbiBinaryTransactionReader(data.toRawHexString())

        val sealedMessage = transactionReader.readAnchorLinkSealedMessage()
        println(sealedMessage)
        println(sealedMessage.bytes.toRawHexString())

        val receivePrivateKey = eosKeyManager.getPrivateKey(session.receiveKey)
        val requestPublicKey = EosPublicKey(session.requestKey)

        return decodeData(
            sealedMessage.bytes,
            sealedMessage.nonce,
            receivePrivateKey,
            requestPublicKey
        ).decodeToString()
    }

    fun decodeData(
        data: ByteArray,
        nonce: ULong,
        privateKey: EosPrivateKey,
        publicKey: EosPublicKey
    ): ByteArray {
        val sharedSecret = KeyUtils.deriveSharedSecret(privateKey, publicKey)

        val byteWriter = DefaultByteWriter()
        byteWriter.putLong(nonce.toLong())
        val encodedNonce = byteWriter.toBytes()
        val key = (encodedNonce + sharedSecret).sha512()
        return AES.decryptAesCbc(
            data,
            key.bytes.copyOfRange(0, 32),
            key.bytes.copyOfRange(32, 48),
            Padding.PKCS7Padding
        )
    }
}