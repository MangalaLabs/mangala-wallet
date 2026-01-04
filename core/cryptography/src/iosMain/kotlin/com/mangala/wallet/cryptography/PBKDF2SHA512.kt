package com.mangala.wallet.cryptography

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cValue
import kotlinx.cinterop.convert
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.refTo
import kotlinx.cinterop.value
import kotlinx.cinterop.write
import platform.CoreCrypto.CCKeyDerivationPBKDF
import platform.CoreCrypto.kCCPBKDF2
import platform.CoreCrypto.kCCPRFHmacAlgSHA256
import platform.CoreCrypto.kCCPRFHmacAlgSHA512
import platform.posix.uint8_tVar

@OptIn(ExperimentalUnsignedTypes::class, ExperimentalForeignApi::class)
actual fun pbkdf2sha512(password: String, salt: String, rounds: Int, derivedKeyLength: Int): ByteArray {
    memScoped {
        val saltData = salt.encodeToByteArray().asUByteArray()
        val derivedKey = UByteArray(derivedKeyLength)

        CCKeyDerivationPBKDF(
            algorithm = kCCPBKDF2,
            password = password,
            passwordLen = password.length.convert(),
            salt = saltData.refTo(0),
            saltLen = saltData.size.convert(),
            prf = kCCPRFHmacAlgSHA512,
            rounds = rounds.toUInt(),
            derivedKey = derivedKey.refTo(0),
            derivedKeyLen = derivedKeyLength.convert()
        )

        return derivedKey.toByteArray()
    }
}