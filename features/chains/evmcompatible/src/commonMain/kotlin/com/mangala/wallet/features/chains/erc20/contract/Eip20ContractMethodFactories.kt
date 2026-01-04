package com.mangala.wallet.features.chains.erc20.contract

import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethodFactories

object Eip20ContractMethodFactories : ContractMethodFactories() {

    init {
        registerMethodFactories(listOf(TransferMethodFactory, ApproveMethodFactory))
    }

}
