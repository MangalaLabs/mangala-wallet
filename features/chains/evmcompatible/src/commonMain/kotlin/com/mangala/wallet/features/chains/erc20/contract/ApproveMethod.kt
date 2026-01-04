package com.mangala.wallet.features.chains.erc20.contract

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.model.Address

class ApproveMethod(val spender: Address, val value: BigInteger) : ContractMethod() {

    override val methodSignature = ApproveMethod.methodSignature
    override fun getArguments() = listOf(spender, value)

    companion object {
        const val methodSignature = "approve(address,uint256)"
    }

}
