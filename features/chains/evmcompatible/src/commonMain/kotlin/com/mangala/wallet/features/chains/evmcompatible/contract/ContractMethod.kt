package com.mangala.wallet.features.chains.evmcompatible.contract

open class ContractMethod {
    val methodId: ByteArray by lazy { ContractMethodHelper.getMethodId(methodSignature) }

    protected open val methodSignature: String = ""

    fun encodedABI(): ByteArray {
        return ContractMethodHelper.encodedABI(methodId, getArguments())
    }

    protected open fun getArguments(): List<Any> = listOf()
}
