package com.mangala.wallet.features.chains.erc721.contract

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.model.Address

class Eip721SafeTransferFromMethod(
    val from: Address,
    val to: Address,
    val tokenId: BigInteger,
) : ContractMethod() {

    override val methodSignature = Companion.methodSignature
    override fun getArguments() = listOf(from, to, tokenId)

    companion object {
        const val methodSignature = "safeTransferFrom(address,address,uint256)"
    }

}