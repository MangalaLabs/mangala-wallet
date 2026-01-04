package com.mangala.wallet.features.chains.evmcompatible.contract

import com.mangala.wallet.features.chains.evmcompatible.model.Address

open class ContractEventInstance(val contractAddress: Address) {

    open fun tags(userAddress: Address): List<String> = listOf()

}