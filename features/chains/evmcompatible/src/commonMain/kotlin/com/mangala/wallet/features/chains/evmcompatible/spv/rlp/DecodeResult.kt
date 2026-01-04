package com.mangala.wallet.features.chains.evmcompatible.spv.rlp

import com.soywiz.krypto.encoding.Hex
import kotlinx.serialization.Serializable

class DecodeResult(val pos: Int, val decoded: Any) {

    override fun toString(): String {
        return asString(this.decoded)
    }

    private fun asString(decoded: Any?): String = when (decoded) {
        is String -> decoded
        is ByteArray -> Hex.encode(decoded)
        is Array<*> -> {
            val result = StringBuilder()
            for (item in decoded) {
                result.append(asString(item))
            }
            result.toString()
        }
        else -> ""
    }
}