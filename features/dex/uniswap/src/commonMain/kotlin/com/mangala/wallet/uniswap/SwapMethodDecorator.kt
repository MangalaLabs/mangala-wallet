package com.mangala.wallet.uniswap

import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.decorations.IMethodDecorator
import com.mangala.wallet.uniswap.contract.SwapContractMethodFactories

class SwapMethodDecorator(private val contractMethodFactories: SwapContractMethodFactories) :
    IMethodDecorator {

    override fun contractMethod(input: ByteArray): ContractMethod? =
        contractMethodFactories.createMethodFromInput(input)

}
