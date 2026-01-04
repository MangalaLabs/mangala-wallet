package com.mangala.wallet.features.chains.erc20.contract

import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethodFactory
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethodHelper
import com.mangala.wallet.features.chains.evmcompatible.core.toBigInteger
import com.mangala.wallet.features.chains.evmcompatible.model.Address

object ApproveMethodFactory : ContractMethodFactory {

    override val methodId = ContractMethodHelper.getMethodId(ApproveMethod.methodSignature)

    override fun createMethod(inputArguments: ByteArray): ApproveMethod {
        val address = Address(inputArguments.copyOfRange(12, 32))
        val value = inputArguments.copyOfRange(32, 64).toBigInteger()

        return ApproveMethod(address, value)
    }

}
