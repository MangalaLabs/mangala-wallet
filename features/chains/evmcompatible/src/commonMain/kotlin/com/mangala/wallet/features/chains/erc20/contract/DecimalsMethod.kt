package com.mangala.wallet.features.chains.erc20.contract

import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod

class DecimalsMethod: ContractMethod() {
    override var methodSignature = "decimals()"
    override fun getArguments() = listOf<Any>()
}
