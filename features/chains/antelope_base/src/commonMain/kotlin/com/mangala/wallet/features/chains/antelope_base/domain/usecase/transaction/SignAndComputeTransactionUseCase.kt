package com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction

import com.mangala.antelope.base.api.model.ComputeTransactionRequest
import com.mangala.antelope.base.domain.usecase.ComputeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter

class SignAndComputeTransactionUseCase(
    private val signTransactionUseCase: SignTransactionUseCase,
    private val computeTransactionUseCase: ComputeTransactionUseCase
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

        return computeTransactionUseCase(
            blockchainType,
            ComputeTransactionRequest.Transaction(
                listOf(signature),
                "none",
                "",
                AbiBinaryGenTransactionWriter(CompressionType.NONE).squishTransactionAbi(
                    transactionAbi
                ).toHex()
            )
        )
    }
}