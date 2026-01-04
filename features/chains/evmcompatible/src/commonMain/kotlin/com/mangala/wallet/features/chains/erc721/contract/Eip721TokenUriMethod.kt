package com.mangala.wallet.features.chains.erc721.contract

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod

class Eip721TokenUriMethod(
    private val tokenId: BigInteger
) : ContractMethod() {

    override val methodSignature = Companion.methodSignature

    override fun getArguments() = listOf(tokenId)

    companion object {
        const val methodSignature = "tokenURI(uint256)"
    }
}