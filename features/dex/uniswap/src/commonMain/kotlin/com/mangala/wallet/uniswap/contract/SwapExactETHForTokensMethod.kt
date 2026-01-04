package com.mangala.wallet.uniswap.contract

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.model.Address

class SwapExactETHForTokensMethod(
    val amountOutMin: BigInteger,
    val path: List<Address>,
    val to: Address,
    val deadline: BigInteger
) : ContractMethod() {

    override val methodSignature = Companion.methodSignature
    override fun getArguments() = listOf(amountOutMin, path, to, deadline)

    companion object {
        const val methodSignature = "swapExactETHForTokens(uint256,address[],address,uint256)"
    }
}
