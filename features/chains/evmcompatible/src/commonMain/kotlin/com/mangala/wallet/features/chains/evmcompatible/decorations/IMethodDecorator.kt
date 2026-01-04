package com.mangala.wallet.features.chains.evmcompatible.decorations

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractEventInstance
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.InternalTransaction
import com.mangala.wallet.features.chains.evmcompatible.model.Transaction
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionLog

interface IMethodDecorator {
    fun contractMethod(input: ByteArray): ContractMethod?
}

interface IEventDecorator {
    fun contractEventInstancesMap(transactions: List<Transaction>): Map<String, List<ContractEventInstance>>
    fun contractEventInstances(logs: List<TransactionLog>): List<ContractEventInstance>
}

interface ITransactionDecorator {
    fun decoration(
        from: Address?,
        to: Address?,
        value: BigInteger?,
        contractMethod: ContractMethod?,
        internalTransactions: List<InternalTransaction>,
        eventInstances: List<ContractEventInstance>
    ): TransactionDecoration?
}