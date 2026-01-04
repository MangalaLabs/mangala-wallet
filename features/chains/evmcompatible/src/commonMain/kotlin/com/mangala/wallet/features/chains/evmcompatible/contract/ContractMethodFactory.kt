package com.mangala.wallet.features.chains.evmcompatible.contract

interface ContractMethodFactory {

    val methodId: ByteArray
    fun createMethod(inputArguments: ByteArray): ContractMethod

}

interface ContractMethodsFactory : ContractMethodFactory {
    override val methodId: ByteArray
        get() = byteArrayOf()

    val methodIds: List<ByteArray>
}
