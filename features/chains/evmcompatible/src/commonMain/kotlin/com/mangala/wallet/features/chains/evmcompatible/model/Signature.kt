package com.mangala.wallet.features.chains.evmcompatible.model

import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import kotlinx.serialization.Serializable

@Serializable // TODO: Refactor don't pollute the model with serialization annotations
class Signature(
    val v: Int,
    val r: ByteArray,
    val s: ByteArray
) {
    override fun toString(): String {
        return "Signature [v: $v; r: ${r.toHexString()}; s: ${s.toHexString()}]"
    }
}
