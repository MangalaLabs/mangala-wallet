package com.mangala.wallet.features.chains.evmcompatible.contract

import com.mangala.wallet.features.chains.evmcompatible.crypto.CryptoUtils

class ContractEvent(
    private val name: String,
    private val arguments: List<Argument>
) {

    val signature: ByteArray by lazy {
        val argumentTypes = arguments.joinToString(separator = ",") { it.type }
        val eventSignature = "$name($argumentTypes)"

        CryptoUtils.sha3(eventSignature.encodeToByteArray())
    }

    enum class Argument(val type: String) {
        Uint256("uint256"),
        Uint256Array("uint256[]"),
        Address("address")
    }

}