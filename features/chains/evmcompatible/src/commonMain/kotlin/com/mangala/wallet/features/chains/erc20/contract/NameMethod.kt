package com.mangala.wallet.features.chains.erc20.contract

import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod

class NameMethod: ContractMethod() {
    override var methodSignature = "name()"
    override fun getArguments() = listOf<Any>()
}
