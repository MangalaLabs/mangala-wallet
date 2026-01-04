package com.mangala.wallet.features.chains.erc20.contract

import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethodFactory
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethodHelper
import com.mangala.wallet.features.chains.evmcompatible.core.toBigInteger
import com.mangala.wallet.features.chains.evmcompatible.model.Address

object TransferMethodFactory : ContractMethodFactory {

    override val methodId = ContractMethodHelper.getMethodId(TransferMethod.methodSignature)

    override fun createMethod(inputArguments: ByteArray): TransferMethod {
        val address = Address(inputArguments.copyOfRange(12, 32))
        val value = inputArguments.copyOfRange(32, 64).toBigInteger()

        return TransferMethod(address, value)
    }

}
