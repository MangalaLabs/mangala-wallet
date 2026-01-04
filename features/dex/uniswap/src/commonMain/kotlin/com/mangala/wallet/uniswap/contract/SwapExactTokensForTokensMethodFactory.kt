package com.mangala.wallet.uniswap.contract

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethodFactory
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethodHelper
import com.mangala.wallet.features.chains.evmcompatible.model.Address

object SwapExactTokensForTokensMethodFactory : ContractMethodFactory {
    override val methodId = ContractMethodHelper.getMethodId(SwapExactTokensForTokensMethod.methodSignature)

    override fun createMethod(inputArguments: ByteArray): ContractMethod {
        val parsedArguments = ContractMethodHelper.decodeABI(inputArguments, listOf(BigInteger::class, BigInteger::class, List::class, Address::class, BigInteger::class))
        val amountIn = parsedArguments[0] as BigInteger
        val amountOutMin = parsedArguments[1] as BigInteger
        val path = parsedArguments[2] as List<Address>
        val to = parsedArguments[3] as Address
        val deadline = parsedArguments[4] as BigInteger
        return SwapExactTokensForTokensMethod(amountIn, amountOutMin, path, to, deadline)
    }

}
