package com.mangala.wallet.features.chains.evmcompatible.utils

import com.mangala.wallet.features.chains.evmcompatible.core.stripHexPrefix
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import com.mangala.wallet.features.chains.evmcompatible.core.toRawHexString
import com.mangala.wallet.features.chains.evmcompatible.crypto.CryptoUtils

object EIP55 {

    fun encode(data: ByteArray): String {
        return format(data.toHexString())
    }

    fun format(address: String): String {
        val lowercaseAddress = address.stripHexPrefix().lowercase()
        val addressHash = CryptoUtils.sha3(lowercaseAddress.encodeToByteArray()).toRawHexString()

        val result = StringBuilder(lowercaseAddress.length + 2)

        result.append("0x")

        for (i in lowercaseAddress.indices) {
            if (addressHash[i].toString().toInt(16) >= 8) {
                result.append(lowercaseAddress[i].toString().uppercase())
            } else {
                result.append(lowercaseAddress[i])
            }
        }

        return result.toString()
    }
}
