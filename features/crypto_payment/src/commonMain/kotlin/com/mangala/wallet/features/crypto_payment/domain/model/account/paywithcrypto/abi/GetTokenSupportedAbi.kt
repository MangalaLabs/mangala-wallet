package com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.abi

import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.model.Address

class GetTokenSupportedAbi(val address: Address): ContractMethod() {
    override val methodSignature = Companion.methodSignature
    override fun getArguments() = listOf(address)

    companion object {
        const val methodSignature = "tokenSupportedMap(address)"
    }
}