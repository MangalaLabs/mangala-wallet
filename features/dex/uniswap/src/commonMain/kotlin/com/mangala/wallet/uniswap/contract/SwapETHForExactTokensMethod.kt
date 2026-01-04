package com.mangala.wallet.uniswap.contract

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.model.Address

class SwapETHForExactTokensMethod(
    val amountOut: BigInteger,
    val path: List<Address>,
    val to: Address,
    val deadline: BigInteger
) : ContractMethod() {

    override val methodSignature = Companion.methodSignature
    override fun getArguments() = listOf(amountOut, path, to, deadline)

    companion object {
        const val methodSignature = "swapETHForExactTokens(uint256,address[],address,uint256)"
    }

}
