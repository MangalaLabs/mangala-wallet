package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.keycert

import com.mangala.wallet.features.chains.antelope_base.domain.model.account.keycert.SecurityLevel
import org.spongycastle.crypto.generators.SCrypt

typealias KeyCertKeyAndIV = Pair<ByteArray, ByteArray>

/**
@returns Pair of key and IV, for use in encrypting/ decrypting the EncryptedPrivateKey in the KeyCert
 */
internal fun getKeyCertKeyAndIv(
    password: ByteArray,
    checksum: ByteArray,
    securityLevel: SecurityLevel
): KeyCertKeyAndIV {
    return getKeyCertKeyAndIv(password, checksum, securityLevel.N, securityLevel.r, securityLevel.p)
}

/**
@returns Pair of key and IV, for use in encrypting/ decrypting the EncryptedPrivateKey in the KeyCert
 */
internal fun getKeyCertKeyAndIv(
    password: ByteArray,
    checksum: ByteArray,
    N: Int,
    r: Int,
    p: Int
): KeyCertKeyAndIV {
    val hash = SCrypt.generate(
        P = password,
        S = checksum,
        N = N,
        r = r,
        p = p,
        dkLen = 32 + 16
    )
    val iv = hash.sliceArray(0 until 16)
    val key = hash.sliceArray(16 until 48)

    return Pair(key, iv)
}