package com.mangala.wallet.features.chains.erc20.contract

import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.model.Address

class BalanceOfMethod(val owner: Address) : ContractMethod() {

    override val methodSignature = "balanceOf(address)"
    override fun getArguments() = listOf(owner)

}
