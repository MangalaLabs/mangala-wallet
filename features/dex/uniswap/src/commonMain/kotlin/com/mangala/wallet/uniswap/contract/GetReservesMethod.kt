package com.mangala.wallet.uniswap.contract

import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod

class GetReservesMethod : ContractMethod() {

    override val methodSignature = "getReserves()"
    override fun getArguments() = listOf<Any>()

}
