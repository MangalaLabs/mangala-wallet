package com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.abi

import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.model.Address

class GetAddressAllowanceAbi(
    private val owner: Address,
    private val spender: Address
): ContractMethod() {

    override fun getArguments() = listOf(owner, spender)

    override val methodSignature = Companion.methodSignature

    companion object {
        const val methodSignature = "allowance(address,address)"
    }
}