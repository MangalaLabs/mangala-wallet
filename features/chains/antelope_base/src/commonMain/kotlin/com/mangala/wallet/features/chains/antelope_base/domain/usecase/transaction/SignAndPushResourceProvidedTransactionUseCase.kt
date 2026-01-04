package com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction

import com.mangala.antelope.base.api.model.PushTransactionRequest
import com.mangala.antelope.base.domain.model.Transaction
import com.mangala.antelope.base.domain.usecase.PushTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.ext.toInstant
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter

class SignAndPushResourceProvidedTransactionUseCase(
    private val signTransactionUseCase: SignTransactionUseCase,
    private val pushTransactionUseCase: PushTransactionUseCase,
) {
    suspend operator fun invoke(
        transaction: Transaction,
        actor: String,
        permissionName: String,
        blockchainType: BlockchainType
    ): Result<String> {
        val chainId = blockchainType.chainId

        val transactionAbi = TransactionAbi(
            (transaction.expiration + 'Z').toInstant(),
            transaction.refBlockNum.toInt(),
            transaction.refBlockPrefix,
            transaction.maxNetUsageWords,
            transaction.maxCpuUsageMs,
            transaction.delaySecs,
            emptyList(),
            transaction.actions.map {
                ActionAbi(
                    it.account,
                    it.name,
                    it.authorization.map {
                        TransactionAuthorizationAbi(
                            it.actor,
                            it.permission
                        )
                    },
                    it.data
                )
            },
            emptyList(),
            transaction.signatures,
            emptyList()
        )

        val signature = signTransactionUseCase(
            chainId = chainId,
            transactionAbi = transactionAbi,
            actor = actor,
            permissionName = permissionName,
            blockchainUid = blockchainType.uid
        )

        return pushTransactionUseCase.withResult(
            blockchainType,
            PushTransactionRequest(
                transaction.signatures + listOf(signature),
                "none",
                "",
                AbiBinaryGenTransactionWriter(CompressionType.NONE).squishTransactionAbi(
                    transactionAbi
                ).toHex()
            )
        )
    }
}