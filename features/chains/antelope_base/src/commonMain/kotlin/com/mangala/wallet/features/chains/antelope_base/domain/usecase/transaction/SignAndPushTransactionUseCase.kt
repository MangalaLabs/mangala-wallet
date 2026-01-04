package com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction

import com.mangala.antelope.base.api.model.PushTransactionRequest
import com.mangala.antelope.base.domain.usecase.PushTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.toPackedTrx
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter

class SignAndPushTransactionUseCase(
    private val signTransactionUseCase: SignTransactionUseCase,
    private val pushTransactionUseCase: PushTransactionUseCase
) {
    suspend operator fun invoke(
        signTransactionRequest: SignTransactionRequest,
        actor: String,
        permissionName: String,
        blockchainType: BlockchainType
    ): Result<String> {
        val transactionAbi = signTransactionRequest.toTransactionAbi()

        val signature = signTransactionUseCase(
            chainId = signTransactionRequest.chainId,
            transactionAbi = transactionAbi,
            actor = actor,
            permissionName = permissionName,
            blockchainUid = blockchainType.uid
        )

        return pushTransactionUseCase.withResult(
            blockchainType,
            PushTransactionRequest(
                listOf(signature),
                "none",
                "",
                transactionAbi.toPackedTrx()
            )
        )
    }
}