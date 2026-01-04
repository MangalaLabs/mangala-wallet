package com.mangala.wallet.features.chains.antelope_base.domain.utils

import com.memtrip.eos.core.crypto.EosPrivateKey
import com.soywiz.krypto.sha256
import io.ktor.utils.io.core.toByteArray

fun EosPrivateKey.getChecksum(): ByteArray {
    val publicKeyStringBytes = this.publicKey.toString().toByteArray()
    val publicKeyHash = publicKeyStringBytes.sha256().bytes
    val publicKeyDoubleHash = publicKeyHash.sha256().bytes

    return publicKeyDoubleHash.sliceArray(0 until 4)
}