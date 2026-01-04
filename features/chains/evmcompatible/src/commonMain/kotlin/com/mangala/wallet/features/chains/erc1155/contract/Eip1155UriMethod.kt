package com.mangala.wallet.features.chains.erc1155.contract

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod

class Eip1155UriMethod(
    private val tokenId: BigInteger
) : ContractMethod() {

    override val methodSignature = Companion.methodSignature

    override fun getArguments() = listOf(tokenId)

    companion object {
        const val methodSignature = "uri(uint256)"
    }
}