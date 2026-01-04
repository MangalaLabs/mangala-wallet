package com.mangala.wallet.uniswap.contract

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethodFactory
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethodHelper
import com.mangala.wallet.features.chains.evmcompatible.model.Address

object SwapETHForExactTokensMethodFactory : ContractMethodFactory {

    override val methodId = ContractMethodHelper.getMethodId(SwapETHForExactTokensMethod.methodSignature)

    override fun createMethod(inputArguments: ByteArray): ContractMethod {
        val parsedArguments = ContractMethodHelper.decodeABI(inputArguments, listOf(BigInteger::class, List::class, Address::class, BigInteger::class))
        val amountOut = parsedArguments[0] as BigInteger
        val path = parsedArguments[1] as List<Address>
        val to = parsedArguments[2] as Address
        val deadline = parsedArguments[3] as BigInteger
        return SwapETHForExactTokensMethod(amountOut, path, to, deadline)
    }

}
