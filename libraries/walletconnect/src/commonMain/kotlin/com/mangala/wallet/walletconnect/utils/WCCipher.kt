/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.utils

import com.mangala.wallet.walletconnect.entity.WCEncryptedPayload
import com.soywiz.krypto.AES
import com.soywiz.krypto.CipherPadding
import com.soywiz.krypto.HMAC
import com.soywiz.krypto.encoding.fromHex
import io.ktor.utils.io.core.*

internal object WCCipher {
    fun decrypt(payload: String, key: String): String{
        val payloadObj = JSON.decodeFromString(WCEncryptedPayload.serializer(), payload)
        val computedHmac = HMAC.hmacSHA256(
            key = key.fromHex(),
            data = payloadObj.data.fromHex() + payloadObj.iv.fromHex(),
        ).hex
        if (computedHmac != payloadObj.hmac) {
            throw Error("calculated hmac:$computedHmac is not equals to hmac:${payloadObj.hmac}")
        }
        val decryptData = AES.decryptAesCbc(
            data = payloadObj.data.fromHex(),
            key = key.fromHex(),
            iv = payloadObj.iv.fromHex(),
            padding = CipherPadding.PKCS7Padding
        )
        return decryptData.decodeToString()
    }

    fun encrypt(payload: String, key: String): WCEncryptedPayload {
        val iv = createRandomBytes(16)
        val encryptedData = AES.encryptAesCbc(
            data = payload.toByteArray(),
            key = key.fromHex(),
            iv = iv,
            padding = CipherPadding.PKCS7Padding
        )

        return WCEncryptedPayload(
            data = encryptedData.hex(),
            iv = iv.hex(),
            hmac = HMAC.hmacSHA256(
                key = key.fromHex(),
                data = encryptedData + iv
            ).hex
        )
    }

    private fun createRandomBytes(i: Int) = ByteArray(i).also { com.soywiz.krypto.SecureRandom.nextBytes(it) }

}
